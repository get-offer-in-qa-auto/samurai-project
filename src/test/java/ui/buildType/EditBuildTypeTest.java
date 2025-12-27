package ui.buildType;

import api.models.project.CreateProjectResponse;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import common.annotations.WithAdminSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.buildType.BuildTypeEditPage;

import static api.generators.RandomData.getBuildName;

public class EditBuildTypeTest extends BaseUiTest {

    @Test
    @WithAdminSession
    @DisplayName("Администратор может поменять имя build configuration")
    public void adminCanEditNameBuildType() {
        String newName = getBuildName();
        CreateProjectResponse projectForTest = UserSteps.createProjectManually(RequestSpecs.adminAuthSpec());
        String idProject = projectForTest.getId();
        String buildIdFirst = UserSteps.createBuildType(idProject, RequestSpecs.adminAuthSpec());
        String nameFirst = UserSteps.getBuildTypeById(buildIdFirst, RequestSpecs.adminAuthSpec());

        BuildTypeEditPage page = new BuildTypeEditPage()
                .openForBuildType(buildIdFirst)
                .editBuildTypeName(nameFirst, newName);
        page.shouldSeeSuccessMessage();

        String actualName = UserSteps.getBuildTypeById(buildIdFirst, RequestSpecs.adminAuthSpec());
        softly.assertThat(actualName).isEqualTo(newName);
    }

    @Test
    @WithAdminSession
    @DisplayName("Администратор не может поменять имя build configuration, имя существует")
    public void adminCanNotEditNameBuildType() {
        CreateProjectResponse projectForTest = UserSteps.createProjectManually(RequestSpecs.adminAuthSpec());
        String idProject = projectForTest.getId();
        String buildIdFirst = UserSteps.createBuildType(idProject, RequestSpecs.adminAuthSpec());
        String buildIdSecond = UserSteps.createBuildType(idProject, RequestSpecs.adminAuthSpec());
        String nameFirst = UserSteps.getBuildTypeById(buildIdFirst, RequestSpecs.adminAuthSpec());
        String nameSecond = UserSteps.getBuildTypeById(buildIdSecond, RequestSpecs.adminAuthSpec());

        BuildTypeEditPage page = new BuildTypeEditPage()
                .openForBuildType(buildIdSecond)
                .editBuildTypeName(nameSecond, nameFirst)
                .shouldSeeDuplicateNameError(nameFirst);

        String actualName = UserSteps.getBuildTypeById(buildIdSecond, RequestSpecs.adminAuthSpec());
        softly.assertThat(actualName).isEqualTo(nameSecond);
    }
}
