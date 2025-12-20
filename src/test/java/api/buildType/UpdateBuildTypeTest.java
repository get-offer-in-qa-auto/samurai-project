package api.buildType;

import api.BaseTest;
import api.models.error.ErrorResponse;
import api.models.project.CreateProjectResponse;
import api.models.users.Roles;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.WithAuthUser;
import common.messages.BuildTypeErrorMessage;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static api.generators.RandomData.getProjectName;

public class UpdateBuildTypeTest extends BaseTest {

    @Test
    @DisplayName("Успешное обновление имени")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanUpdateBuildTypeName() {
        CreateProjectResponse projectForTest = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());
        String idProject = projectForTest.getId();
        String buildId = UserSteps.createBuildType(idProject, RequestSpecs.userAuthSpecWithToken());
        String randomText = getProjectName();

        var response = new ValidatedCrudRequester<>(
                RequestSpecs.userAuthTextSpecWithToken(),
                Endpoint.UPDATE_BUILD_CONFIGURATION_NAME,
                ResponseSpecs.requestReturnsOK())
                .put(buildId, randomText);
        String name = UserSteps.getBuildTypeById(buildId, RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(response).isEqualTo(randomText);
        softly.assertThat(name).isEqualTo(randomText);

    }

    @Test
    @DisplayName("Неуспешное обновление имени, имя уже существует")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanNotUpdateBuildTypeName() {
        CreateProjectResponse projectForTest = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());
        String idProject = projectForTest.getId();
        String buildIdFirst = UserSteps.createBuildType(idProject, RequestSpecs.userAuthSpecWithToken());
        String buildIdSecond = UserSteps.createBuildType(idProject, RequestSpecs.userAuthSpecWithToken());
        String name = UserSteps.getBuildTypeById(buildIdFirst, RequestSpecs.userAuthSpecWithToken());

        ValidatableResponse response = new CrudRequester(
                RequestSpecs.userAuthTextSpecWithToken(),
                Endpoint.UPDATE_BUILD_CONFIGURATION_NAME,
                ResponseSpecs.requestReturns400BadRequest())
                .put(buildIdSecond, name);

        ErrorResponse errorResponse = extractError(response);
        assertErrorMessage(errorResponse, BuildTypeErrorMessage.BUILD_TYPE_NAME_ALREADY_USE);
    }
}