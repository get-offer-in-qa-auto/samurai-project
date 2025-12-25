package ui.projects;

import api.models.project.CreateProjectResponse;
import api.models.project.GetProjectsResponse;
import api.models.users.Roles;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import common.annotations.WithAuthUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.project.AllProjectsPage;

import java.util.List;

public class DeleteProjectTest extends BaseUiTest {

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @DisplayName("Удаление проекта")
    public void userCanDeleteProject() {
        CreateProjectResponse project = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());
        var name = project.getName();

        new AllProjectsPage().open()
                .openCertainProject(name)
                .openProjectSettingsPage()
                .deleteProject();

        softly.assertThat(new AllProjectsPage().projectShouldNotPresent(name)).isTrue();

        List<GetProjectsResponse.Project> projects = UserSteps.getProjects(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(projects).as("Проверка через API, что проект удалился")
                .extracting(GetProjectsResponse.Project::getId)
                .doesNotContain(name);
    }
}
