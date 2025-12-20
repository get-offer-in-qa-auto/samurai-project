package api.buildType;

import api.BaseTest;
import api.comparison.ModelAssertions;
import api.models.buildConfiguration.CreateBuildTypeRequest;
import api.models.buildConfiguration.CreateBuildTypeResponse;
import api.models.buildConfiguration.Project;
import api.models.error.ErrorResponse;
import api.models.project.CreateProjectResponse;
import api.models.users.Roles;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.WithAuthUser;
import common.messages.BuildTypeErrorMessage;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;


public class CreateBuildTypeTest extends BaseTest {

    public static Stream<Arguments> validBuildConfigurationName() {
        return Stream.of(
                //длинное name + англ. буквы
                Arguments.of("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqwqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq"),
                //короткое name + русские буквы
                Arguments.of("Й"),
                //спецсимволы
                Arguments.of("!@#$%^&*"),
                // 3 пробела
                Arguments.of("   ")
        );
    }

    @ParameterizedTest
    @DisplayName("Успешное создание BuildType")
    @MethodSource("validBuildConfigurationName")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanCreatedBuildConfiguration(String name) {
        CreateProjectResponse projectForTest = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());
        String idProject = projectForTest.getId();
        Project project = Project.builder()
                .id(idProject)
                .build();

        CreateBuildTypeRequest createBuildTypeRequest = CreateBuildTypeRequest.builder()
                .name(name)
                .project(project)
                .build();

        CreateBuildTypeResponse response = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.BUILD_CONFIGURATION_CREATE,
                ResponseSpecs.requestReturnsOK())
                .post(createBuildTypeRequest)
                .extract()
                .as(CreateBuildTypeResponse.class);

        ModelAssertions.assertThatModels(createBuildTypeRequest, response);
        String buildId = response.getId();
        String nameActual = UserSteps.getBuildTypeById(buildId, RequestSpecs.userAuthSpecWithToken());

        softly.assertThat(nameActual).isEqualTo(name);
    }


    @Test
    @DisplayName("Неуспешное создание BuildType, пустое поле name")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanNotCreatedBuildConfigurationWithEmptyName() {
        CreateProjectResponse projectForTest = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());
        String idProject = projectForTest.getId();
        Project project = Project.builder()
                .id(idProject)
                .build();

        CreateBuildTypeRequest createBuildTypeRequest = CreateBuildTypeRequest.builder()
                .name("")
                .project(project)
                .build();

        ValidatableResponse response = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.BUILD_CONFIGURATION_CREATE,
                ResponseSpecs.requestReturns400BadRequest())
                .post(createBuildTypeRequest);

        ErrorResponse errorResponse = extractError(response);
        assertErrorMessage(errorResponse, BuildTypeErrorMessage.BUILD_TYPE_NAME_EMPTY);
    }
}
