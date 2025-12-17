package api.builds;

import api.BaseTest;
import api.models.buildConfiguration.BuildType;
import api.models.builds.*;
import api.models.error.ErrorResponse;
import api.models.project.CreateProjectResponse;
import api.models.users.Roles;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.BuildSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.WithAuthUser;
import common.errors.BuildErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static common.errors.BuildErrorMessage.NOTHING_FOUND_BY_LOCATOR;
import static common.errors.BuildErrorMessage.NO_BUILD_TYPE_NOR_TEMPLATE_IS_FOUND_BY_ID;

public class BuildTestNegative extends BaseTest {

    public static Stream<Arguments> invalidBuildType(){
        return Stream.of(
                Arguments.of("", NOTHING_FOUND_BY_LOCATOR),
                Arguments.of("   ", NO_BUILD_TYPE_NOR_TEMPLATE_IS_FOUND_BY_ID)
        );
    }

    //Тест 1: создать билд с невалидным buildType
    @ParameterizedTest
    @MethodSource("invalidBuildType")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanNotCreateBuildWithInvalidBuildType(String buildTypeId, BuildErrorMessage expectedError) {
//создаю проект
        CreateProjectResponse project =
                UserSteps.createProjectManually(RequestSpecs.adminAuthSpec());

        //записываю, сколько билдов в очереди ДО
        int before = BuildSteps.getBuildQueue().getCount();


    //не создаю buildConfiguration, он подается извне, создаю сам билд
        CreateBuildRequest build = CreateBuildRequest.builder()
                .buildType(
                        BuildType.builder()
                                .id(buildTypeId)
                                .build()
                )
                .build();

        var response = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.requestReturns404NotFound()).post(build);

        ErrorResponse errorResponse = extractError(response);
        softly.assertThat(errorResponse.getErrors()).isNotEmpty();
        assertErrorMessageContains(errorResponse, expectedError);

        //проверка, что билд не создался: вызовем очередь и проверим, добавилось ли в нее что-то новое и сравним
        int after = BuildSteps.getBuildQueue().getCount();
        softly.assertThat(before).isEqualTo(after);
    }

    public static Stream<Arguments> invalidBuildId(){
        return Stream.of(
                Arguments.of(Integer.MAX_VALUE),
                Arguments.of(Integer.MIN_VALUE)
        );

    }

    //Тест 2: попытаться вернуть билд из очереди, используя невалидный id/вернуть несуществующий билд
    @ParameterizedTest
    @MethodSource("invalidBuildId")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCantGetNonExitedBuild(Integer invalidId){
        //создаю проект
        CreateProjectResponse project =
                UserSteps.createProjectManually(RequestSpecs.adminAuthSpec());
        var response = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.GET_BUILD,
                ResponseSpecs.requestReturns404NotFound()).get(invalidId);

        ErrorResponse errorResponse = extractError(response);

        softly.assertThat(errorResponse.getErrors()).isNotEmpty();
        softly.assertThat(errorResponse.getErrors().getFirst().getMessage())
                .contains(BuildErrorMessage.NO_BUILD_FOUND_BY_ID.getMessage());
    }

    //Тест 3: удалить несуществующий билд
    @ParameterizedTest
    @MethodSource("invalidBuildId")
    @WithAuthUser(role = Roles.AGENT_MANAGER)

    public void userCantDeleteNonExistedBuild(Integer invalidId){
        //создаю проект
        CreateProjectResponse project =
                UserSteps.createProjectManually(RequestSpecs.adminAuthSpec());

        var response= new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.DELETE_BUILD_FROM_QUEUE,
                ResponseSpecs.requestReturns404NotFound()).delete(invalidId);
        ErrorResponse errorResponse = extractError(response);

        softly.assertThat(errorResponse.getErrors()).isNotEmpty();
        softly.assertThat(errorResponse.getErrors().getFirst().getMessage())
                .contains(BuildErrorMessage.NO_BUILD_FOUND_BY_ID.getMessage());
    }

    //Тест 4: отменить несуществующий билд
    @ParameterizedTest
    @MethodSource("invalidBuildId")
    @DisplayName("Cant cancel non-existed build from queue via API")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCantCancelNonExistedBuild(Integer invalidId, TestInfo testInfo){
        //создаю проект
        CreateProjectResponse project =
                UserSteps.createProjectManually(RequestSpecs.adminAuthSpec());

        //подготовили комментарий
        String comment = BuildSteps.prepareCommentForCancellingBuild(testInfo.getDisplayName());
        CancelBuildRequest cancelBody = CancelBuildRequest.builder()
                .comment(comment)
                .readdIntoQueue(false)
                .build();

        var response = new CrudRequester(
                RequestSpecs.adminAuthSpec(),
                Endpoint.CANCEL_BUILD,
                ResponseSpecs.requestReturns404NotFound()).post(cancelBody, invalidId);
        ErrorResponse errorResponse = extractError(response);

        softly.assertThat(errorResponse.getErrors()).isNotEmpty();
        softly.assertThat(errorResponse.getErrors().getFirst().getMessage())
                .contains(BuildErrorMessage.NO_BUILD_FOUND_BY_ID.getMessage());

    }

    //Тест 5: удалить удаленный билд
    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCannotDeleteBuildTwice(){
        //создать проект
        CreateProjectResponse createdProject = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());

        //создать buildType в нем
        String createdBuildType = UserSteps.createBuildType(createdProject.getId(), RequestSpecs.userAuthSpecWithToken());

        //создать билд
        CreateBuildResponse createdBuild = BuildSteps.addBuildToQueue(createdBuildType);

       //удаляем билд
        BuildSteps.deleteBuildFromQueue(createdBuild);

      //убедиться, что билд удален
       BuildSteps.checkIfBuildHasAlreadyDeleted(createdBuild);

      //Пытаемся удалить еще раз
        var response = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.DELETE_BUILD_FROM_QUEUE,
                ResponseSpecs.requestReturns404NotFound()).delete(createdBuild.getId());

        ErrorResponse errorResponse = extractError(response);

        softly.assertThat(errorResponse.getErrors()).isNotEmpty();
        softly.assertThat(errorResponse.getErrors().getFirst().getMessage())
                .contains(BuildErrorMessage.NO_BUILD_FOUND_BY_ID.getMessage());
    }

    //Тест 6: отменить уже отмененный билд -- кажется, здесь ошибка, потому что всегда будет 200
    @Test
    @DisplayName("Cancel build from queue via API")
    @WithAuthUser(role = Roles.AGENT_MANAGER)

    public void userCannotCancelBuildTwice(TestInfo testInfo){
        //создать проект
        CreateProjectResponse createdProject = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());

        //создать buildType в нем
        String createdBuildType = UserSteps.createBuildType(createdProject.getId(), RequestSpecs.userAuthSpecWithToken());

        //создать билд
        CreateBuildResponse createdBuild = BuildSteps.addBuildToQueue(createdBuildType);

        //отменяет билд
        BuildSteps.cancelBuild(createdBuild);

         //отменить отмененный билд
        //подготовили комментарий
        String comment = BuildSteps.prepareCommentForCancellingBuild(testInfo.getDisplayName());

        CancelBuildRequest cancelBody = CancelBuildRequest.builder()
                .comment(comment)
                .readdIntoQueue(false)
                .build();

        CancelBuildResponse canceledBuild = new ValidatedCrudRequester<CancelBuildResponse>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.CANCEL_BUILD,
                ResponseSpecs.requestReturnsOK()).post(createdBuild, createdBuild.getId()); //кажется, это ошибка?
    }
}
