package ui.builds;

import api.models.builds.CreateBuildResponse;
import api.models.project.CreateProjectResponse;
import api.models.users.Roles;
import api.requests.steps.BuildSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import common.annotations.WithAuthUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.builds.BuildPage;
import ui.pages.builds.ProjectPage;

public class BuildTest extends BaseUiTest {
    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @DisplayName("Юзер добавляет билд в очередь")
    void userCanRunBuild() {

        CreateProjectResponse project =
                UserSteps.createProjectManually(RequestSpecs.adminAuthSpec());

        String buildTypeId = UserSteps.createBuildType(project.getId(), RequestSpecs.adminAuthSpec());

        CreateBuildResponse createdBuild = BuildSteps.addBuildToQueue(buildTypeId);

        String projectId = project.getId();

        new ProjectPage()
                .openProjectById(projectId);
        new ProjectPage()
                .runBuild();

        //проверим на UI что элемент добавился
        new ProjectPage().checkIfBuildIsAdded();
        //проверим в апи
        BuildSteps.getBuildFromQueue(createdBuild);

    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @DisplayName("Юзер отменяет билд, который уже в очереди")
    public void userCanCancelBuildFromQueue() {
        CreateProjectResponse project =
                UserSteps.createProjectManually(RequestSpecs.adminAuthSpec());

        String buildTypeId = UserSteps.createBuildType(project.getId(), RequestSpecs.adminAuthSpec());

        CreateBuildResponse createBuildResponse = BuildSteps.addBuildToQueue(buildTypeId);
        String projectId = project.getId();

        new ProjectPage()
                .openProjectById(projectId);

        new ProjectPage()
                .runBuild();

        new BuildPage()
                .openPage(createBuildResponse)
                .stopBuild()
                .findModal()
                .cancelBuildFromQueue();

        //проверим через UI
        new BuildPage().checkIfBuildIsCancelled();

        //проверим через апи
        BuildSteps.checkIfBuildIsAlreadyCanceled(createBuildResponse);
    }
}
