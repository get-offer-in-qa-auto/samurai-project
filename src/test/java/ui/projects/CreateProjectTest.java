package ui.projects;

import api.generators.RandomData;
import api.generators.RandomModelGenerator;
import api.models.project.CreateProjectManuallyRequest;
import api.models.project.GetProjectsResponse.Project;
import api.models.users.Roles;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import common.annotations.WithAuthUser;
import common.messages.ProjectUiMessage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.project.AllProjectsPage;

import java.util.List;

import static common.messages.ProjectUiMessage.PROJECT_CREATED;

public class CreateProjectTest extends BaseUiTest {

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @DisplayName("Успешное создание проекта мануально")
    public void userCanCreateProjectManually() {
        CreateProjectManuallyRequest projectModel = RandomModelGenerator.generate(CreateProjectManuallyRequest.class);

        var resultMsg = new AllProjectsPage().open().openCreationProjectPage()
                .createProjectManually(RandomData.getProjectName(), projectModel.getId())
                .getMessageForManually();
        softly.assertThat(resultMsg).as("Проверка сообщения об успешном создании проекта")
                .contains(PROJECT_CREATED.getMessage());

        List<Project> projects = UserSteps.getProjects(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(projects).as("Проверка через API, что проект создался")
                .extracting(Project::getId)
                .containsOnlyOnce(projectModel.getId());
    }

    @Test
    @Disabled("Нa сi ui нет данного выбора")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @DisplayName("Успешное создание проекта из репозитория")
    public void userCanCreateProjectFromRepository() {
        var projectName = RandomData.getProjectName();

        var resultMsg = new AllProjectsPage().open().openCreationProjectPage()
                .createProjectFromRepository(projectName)
                .getMessageForFromRepository();
        softly.assertThat(resultMsg).as("Проверка сообщения об успешном создании проекта")
                .contains(PROJECT_CREATED.getMessage());

        List<Project> projects = UserSteps.getProjects(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(projects).as("Проверка через API, что проект создался")
                .extracting(Project::getName)
                .containsOnlyOnce(projectName);
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @DisplayName("Неспешное создание проекта с пустым id")
    public void userCanNotCreateProjectManuallyWithEmptyId() {
        var projectName = RandomData.getProjectName();
        var resultMsg = new AllProjectsPage().open().openCreationProjectPage()
                .inputData(projectName, "")
                .getUnsuccessIdMessageForManually();
        softly.assertThat(resultMsg).as("Проверка сообщения об ошибке")
                .isEqualTo(ProjectUiMessage.ID_MUST_NOT_BE_EMPTY.getMessage());

        List<Project> projects = UserSteps.getProjects(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(projects).as("Проверка через API, что проект не создался")
                .extracting(Project::getId)
                .doesNotContain(projectName);
    }
}
