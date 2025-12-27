package api.projects;

import api.BaseTest;
import api.comparison.ModelAssertions;
import api.models.project.CreateProjectResponse;
import api.models.project.GetProjectsResponse;
import api.models.users.Roles;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.WithAuthUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static api.generators.RandomData.getProjectName;

public class GetProjectTest extends BaseTest {

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @DisplayName("Получение проекта")
    public void userCanGetProjectById() {
        CreateProjectResponse createdProject = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());
        GetProjectsResponse.Project gettedProject = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.GET_PROJECT_BY_ID,
                ResponseSpecs.requestReturnsOK())
                .get(createdProject.id)
                .extract()
                .as(GetProjectsResponse.class)
                .getProject()
                .getFirst();

        ModelAssertions.assertThatModels(createdProject, gettedProject).match();
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @DisplayName("Невозможность получения проекта с невалидным id")
    public void userCannotGetProjectWithInvalidId() {
        int matchedCount = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.GET_PROJECT_BY_ID,
                ResponseSpecs.requestReturnsOK())
                .get(getProjectName())
                .extract()
                .as(GetProjectsResponse.class)
                .getCount();

        softly.assertThat(matchedCount).isEqualTo(0);
    }
}
