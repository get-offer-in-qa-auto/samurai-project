package api.projects;

import api.BaseTest;
import api.models.error.ErrorResponse;
import api.models.project.CreateProjectResponse;
import api.models.users.Roles;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.WithAuthUser;
import common.messages.ProjectErrorMessage;
import org.junit.jupiter.api.Test;

import static api.generators.RandomData.getProjectName;

public class DeleteProjectTest extends BaseTest {

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanDeleteProject() {
        CreateProjectResponse project = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());
        int initialCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());

        new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_DELETE,
                ResponseSpecs.requestReturnsNoContent())
                .deleteById(project.getId());

        int finalCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(finalCount).isEqualTo(initialCount - 1);
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCannotDeleteProjectWithInvalidId() {
        int initialCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());

        var projectDelResponse = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_DELETE,
                ResponseSpecs.requestReturns404NotFound())
                .deleteById(getProjectName());

        int finalCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(finalCount).isEqualTo(initialCount);
        ErrorResponse errorResponse = extractError(projectDelResponse);
        assertErrorMessageContains(errorResponse, ProjectErrorMessage.PROJECT_NOT_FOUND);
    }
}
