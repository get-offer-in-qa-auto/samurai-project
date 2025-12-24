package api.buildType;

import api.BaseTest;
import api.models.project.CreateProjectResponse;
import api.models.users.Roles;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.WithAuthUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DeleteBuildTypeTest extends BaseTest {

    @Test
    @DisplayName("Успешное удаление Build Type")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanDeleteBuildType() {
        CreateProjectResponse projectForTest = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());
        String idProject = projectForTest.getId();
        int countBuildTypeBefore = UserSteps.getBuildTypeCount(RequestSpecs.userAuthSpecWithToken());
        String buildId = UserSteps.createBuildType(idProject, RequestSpecs.userAuthSpecWithToken());

        new CrudRequester(
                RequestSpecs.userAuthTextSpecWithToken(),
                Endpoint.DELETE_BUILD_CONFIGURATION,
                ResponseSpecs.requestReturnsNoContent())
                .deleteById(buildId);

        int countBuildTypeAfter = UserSteps.getBuildTypeCount(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(countBuildTypeBefore).isEqualTo(countBuildTypeAfter);
    }
}
