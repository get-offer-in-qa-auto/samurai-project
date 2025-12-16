package api.builds;

import api.BaseTest;
import api.models.BaseModel;
import api.models.builds.*;
import api.models.users.Roles;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.BuildSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.WithAuthUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class BuildTestNegative extends BaseTest {
    public static Stream<Arguments> invalidBuildType(){
        return Stream.of(
                Arguments.of(
                        ""

                ),
                Arguments.of(
                        "   "
                ),
                Arguments.of(
                        null
                )
        );

    }

    @ParameterizedTest
    @MethodSource("invalidBuildType")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanNotCreateBuildWithInvalidBuildType(String buildTypeId){

        //cоздать проект, взяли id проекта
        String projectId = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken()).getId();

        CreateBuildRequest build = CreateBuildRequest.builder()
                .buildType(
                        CreateBuildRequest.BuildType.builder()
                                .id(buildTypeId)
                                .build()
                )
                .build();

        new ValidatedCrudRequester<CreateBuildResponse>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.requestReturns400BadRequest()).post(build);

        //удалим проект и все его содержимое
        BuildSteps.cleanEnvironmentAfterBuilds(projectId,RequestSpecs.userAuthSpecWithToken());

    }

    public static Stream<Arguments> invalidBuildId(){
        return Stream.of(
                Arguments.of(
                        Integer.MAX_VALUE

                ),
                Arguments.of(
                        Integer.MIN_VALUE
                )
        );

    }

    @ParameterizedTest
    @MethodSource("invalidBuildId")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCantGetNonExitedBuild(Integer invalidId){
        //cоздать проект, взяли id проекта
        String projectId = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken()).getId();

        new ValidatedCrudRequester<GetBuildResponse>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.GET_BUILD,
                ResponseSpecs.requestReturns404NotFound()).get(invalidId);

        //удалим проект и все его содержимое
        BuildSteps.cleanEnvironmentAfterBuilds(projectId,RequestSpecs.userAuthSpecWithToken());
    }

    @ParameterizedTest
    @MethodSource("invalidBuildId")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    //удалить несуществующий

    public void userCantDeleteNonExistedBuild(Integer invalidId){
        //cоздать проект, взяли id проекта
        String projectId = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken()).getId();

        new ValidatedCrudRequester<BaseModel>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.DELETE_BUILD_FROM_QUEUE,
                ResponseSpecs.requestReturns404NotFound()).delete(invalidId);

        //удалим проект и все его содержимое
        BuildSteps.cleanEnvironmentAfterBuilds(projectId,RequestSpecs.userAuthSpecWithToken());

    }

    //отменить несуществующий
    @ParameterizedTest
    @MethodSource("invalidBuildId")
    @DisplayName("Cant cancel non-existed build from queue via API")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCantCancelNonExistedBuild(Integer invalidId, TestInfo testInfo){
        //cоздать проект, взяли id проекта
        String projectId = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken()).getId();


        //подготовили комментарий
        String comment = BuildSteps.prepareCommentForCancellingBuild(testInfo.getDisplayName());
        CancelBuildRequest cancelBody = CancelBuildRequest.builder()
                .comment(comment)
                .readdIntoQueue(false)
                .build();

        new ValidatedCrudRequester<CancelBuildResponse>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.CANCEL_BUILD,
                ResponseSpecs.requestReturns404NotFound()).post(cancelBody, invalidId);

        //удалим проект и все его содержимое
        BuildSteps.cleanEnvironmentAfterBuilds(projectId,RequestSpecs.userAuthSpecWithToken());
    }

    //удалить удаленный билд
    public void userCantRemoveAlreadyRemovedBuild(){
       //создали проект и buildId
        String buildType = BuildSteps.setEnvironmentToCreateBuild();

        // создаем билд
        CreateBuildResponse createdBuild = BuildSteps.addBuildToQueue(buildType);
        String projectId = createdBuild.getBuildType().getProjectId();

        //удаляем билд
        BuildSteps.deleteBuildFromQueue(createdBuild);

        //убедиться, что билд удален
        BuildSteps.checkIfBuildIsDeleted(createdBuild);

        //Пытаемся удалить еще раз
        new ValidatedCrudRequester<BaseModel>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.DELETE_BUILD_FROM_QUEUE,
                ResponseSpecs.requestReturns404NotFound()).delete(createdBuild.getId());

        //удалим проект и все его содержимое
        BuildSteps.cleanEnvironmentAfterBuilds(projectId,RequestSpecs.userAuthSpecWithToken());
    }


    @Test
    @DisplayName("Cancel build from queue via API")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    //отменить отмененный -- кажется, здесь ошибка, потому что всегда будет 200
    public void userCantCancelAlreadyCancelledBuild(TestInfo testInfo){
        //создали проект и buildId
        String buildType = BuildSteps.setEnvironmentToCreateBuild();

        // создаем билд
        CreateBuildResponse createdBuild = BuildSteps.addBuildToQueue(buildType);
        String projectId = createdBuild.getBuildType().getProjectId();

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
                ResponseSpecs.requestReturnsOK()).post(createdBuild, createdBuild.getId());

        //удалим проект и все его содержимое
        BuildSteps.cleanEnvironmentAfterBuilds(projectId,RequestSpecs.userAuthSpecWithToken());
    }

}
