package api.projects;

import api.BaseTest;
import api.models.project.CreateProjectResponse;
import api.models.users.Roles;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.WithAuthUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static api.generators.RandomData.getProjectName;

public class UpdateProjectTest extends BaseTest {

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @DisplayName("Редактировнаие проекта")
    public void userCanUpdateProjectDescription() {
        CreateProjectResponse project = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());
        String projectBefore = UserSteps.getProjectById(project.getId(), RequestSpecs.userAuthSpecWithToken())
                .getDescription();
        var randomText = getProjectName();
        var response = new ValidatedCrudRequester<>(
                RequestSpecs.userAuthTextSpecWithToken(),
                Endpoint.PROJECT_UPDATE,
                ResponseSpecs.requestReturnsOK())
                .put(project.id, randomText);
        softly.assertThat(response).isEqualTo(randomText);

        String projectAfter = UserSteps.getProjectById(project.getId(), RequestSpecs.userAuthSpecWithToken())
                .getDescription();
        softly.assertThat(projectAfter).isNotEqualTo(projectBefore);
        softly.assertThat(projectAfter).isEqualTo(randomText);
    }
}
