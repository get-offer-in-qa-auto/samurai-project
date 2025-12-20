package ui.builds;

import api.models.builds.CreateBuildResponse;
import api.models.project.CreateProjectResponse;
import api.models.users.Roles;
import api.requests.steps.BuildSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import common.annotations.WithAuthUser;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.builds.BuildPage;
import ui.pages.builds.ProjectPage;

public class BuildTests extends BaseUiTest {
    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    void userCanRunBuild() {

        CreateProjectResponse project =
                UserSteps.createProjectManually(RequestSpecs.adminAuthSpec());

        String buildTypeId = UserSteps.createBuildType(project.getId(), RequestSpecs.adminAuthSpec());

        BuildSteps.addBuildToQueue(buildTypeId);

        String projectId = project.getId();

        new ProjectPage()
                .openProjectById(projectId);
        new ProjectPage()
                .runBuild();
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanCancelBuildFromQueue(){
        CreateProjectResponse project =
                UserSteps.createProjectManually(RequestSpecs.adminAuthSpec());

        String buildTypeId = UserSteps.createBuildType(project.getId(), RequestSpecs.adminAuthSpec());

        CreateBuildResponse createBuildResponse= BuildSteps.addBuildToQueue(buildTypeId);
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
    }
}
