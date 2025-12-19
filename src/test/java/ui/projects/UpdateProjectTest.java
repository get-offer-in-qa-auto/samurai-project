package ui.projects;

import api.models.project.CreateProjectResponse;
import api.models.users.Roles;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import common.annotations.WithAuthUser;
import common.messages.ProjectUiMessage;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.project.AllProjectsPage;

import static api.generators.RandomData.getProjectName;

public class UpdateProjectTest extends BaseUiTest {

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanUpdateProject() {
        CreateProjectResponse project = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());
        String descriptionBefore = UserSteps.getProjectById(project.getId(), RequestSpecs.userAuthSpecWithToken())
                .getDescription();
        var newDescription = getProjectName();

        var resultMsg = new AllProjectsPage().open()
                .gotoProject(project.getName())
                .gotoSettings()
                .updateDescription(newDescription)
                .getUpdateMessage();

        softly.assertThat(resultMsg).as("Проверка сообщения об успешном изменении")
                .contains(ProjectUiMessage.CHANGES_SAVED.getMessage());

        String descriptionAfter = UserSteps.getProjectById(project.getId(), RequestSpecs.userAuthSpecWithToken())
                .getDescription();
        softly.assertThat(descriptionAfter).isNotEqualTo(descriptionBefore);
        softly.assertThat(descriptionAfter).isEqualTo(newDescription);

    }
}
