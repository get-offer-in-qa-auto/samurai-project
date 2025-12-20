package ui.BuildTypeTest;

import api.generators.RandomData;
import api.models.project.CreateProjectResponse;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import common.annotations.WithAdminSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.buildType.CreateBuildTypePage;

public class CreateBuildTypeManually extends BaseUiTest {

    @Test
    @WithAdminSession
    @DisplayName("Администратор может создать build configuration вручную")
    public void adminCanCreateBuildTypeManually() {
        final String buildTypeName = RandomData.getBuildName();
        CreateProjectResponse projectForTest = UserSteps.createProjectManually(RequestSpecs.adminAuthSpec());
        String idProject = projectForTest.getId();
        CreateBuildTypePage page = new CreateBuildTypePage()
                .openForProject(idProject);

        String buildTypeId =
                page.createBuildTypeManuallyAndGetId(buildTypeName);
        page.shouldSeeSuccessMessage();
        String nameActual = UserSteps.getBuildTypeById(buildTypeId, RequestSpecs.adminAuthSpec());

        softly.assertThat(nameActual).isEqualTo(buildTypeName);
    }

    @Test
    @WithAdminSession
    @DisplayName("Администратор не может создать build configuration, имя существует")
    public void adminCanNotCreatedTypeManually() {
        CreateProjectResponse projectForTest = UserSteps.createProjectManually(RequestSpecs.adminAuthSpec());
        String idProject = projectForTest.getId();
        String buildIdFirst = UserSteps.createBuildType(idProject, RequestSpecs.adminAuthSpec());
        String buildTypeName = UserSteps.getBuildTypeById(buildIdFirst, RequestSpecs.adminAuthSpec());
        CreateBuildTypePage page = new CreateBuildTypePage()
                .openForProject(idProject)
                .createBuildTypeManually(buildTypeName)
                .shouldSeeDuplicateNameError(buildTypeName);
    }
}
