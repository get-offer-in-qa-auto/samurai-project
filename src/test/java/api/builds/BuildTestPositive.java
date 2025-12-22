package api.builds;

import api.BaseTest;
import api.comparison.ModelAssertions;
import api.models.BaseModel;
import api.models.buildConfiguration.BuildType;
import api.models.builds.*;
import api.models.project.CreateProjectResponse;
import api.models.users.Roles;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.BuildSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.WithAuthUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static common.states.BuildState.*;

public class BuildTestPositive extends BaseTest {

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanCreateBuild() {
        CreateProjectResponse project =
                UserSteps.createProjectManually(RequestSpecs.adminAuthSpec());
        String buildTypeId =
                UserSteps.createBuildType(project.getId(), RequestSpecs.adminAuthSpec());
        //тело запроса
        CreateBuildRequest build = CreateBuildRequest.builder()
                .buildType(
                        BuildType.builder()
                                .id(buildTypeId)
                                .build()
                )
                .build();
        CreateBuildResponse createdBuild = new ValidatedCrudRequester<CreateBuildResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.requestReturnsOK()).post(build);
        ModelAssertions.assertThatModels(build, createdBuild).match();

        softly.assertThat(createdBuild.getBuildType().getId()).isEqualTo(buildTypeId);
        softly.assertThat(createdBuild.getState()).isEqualTo(QUEUED.getMessage());
        softly.assertThat(createdBuild.getId()).isNotNull();
        softly.assertThat(createdBuild.getHref()).isNotBlank();
        softly.assertThat(createdBuild.getWebUrl()).isNotBlank();

        //проверим, что такой билд действительно появился
        GetBuildResponse getBuildResponse = BuildSteps.getBuildFromQueue(createdBuild);
        softly.assertThat(getBuildResponse.getBuildTypeId()).isEqualTo(createdBuild.getBuildType().getId());
        softly.assertThat(getBuildResponse.getId()).isEqualTo(createdBuild.getId());
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanGetBuild() {
        CreateProjectResponse createdProject = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());

        String createdBuildType = UserSteps.createBuildType(createdProject.getId(), RequestSpecs.userAuthSpecWithToken());

        CreateBuildResponse createdBuild = BuildSteps.addBuildToQueue(createdBuildType);
        Integer buildId = createdBuild.getId();

        GetBuildResponse getBuildResponse = new ValidatedCrudRequester<GetBuildResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.GET_BUILD,
                ResponseSpecs.requestReturnsOK()).get(buildId);
        ModelAssertions.assertThatModels(createdBuild, getBuildResponse).match();

        softly.assertThat(getBuildResponse.getId()).isEqualTo(buildId);
        softly.assertThat(getBuildResponse.getBuildType().getId()).isEqualTo(createdBuildType);
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanDeleteBuild() {
        CreateProjectResponse createdProject = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());

        String createdBuildType = UserSteps.createBuildType(createdProject.getId(), RequestSpecs.userAuthSpecWithToken());

        CreateBuildResponse createdBuild = BuildSteps.addBuildToQueue(createdBuildType);

        new ValidatedCrudRequester<BaseModel>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.DELETE_BUILD_FROM_QUEUE,
                ResponseSpecs.requestReturnsNoContent()).deleteNoContent(createdBuild.getId());

        //убедиться, что билд удален (с помощью get)
        BuildSteps.checkIfBuildHasAlreadyDeleted(createdBuild);
    }

    @Test
    @DisplayName("Cancel build from queue via API")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanCancelBuild(TestInfo testInfo) {
        CreateProjectResponse createdProject = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());

        String createdBuildType = UserSteps.createBuildType(createdProject.getId(), RequestSpecs.userAuthSpecWithToken());

        CreateBuildResponse createdBuild = BuildSteps.addBuildToQueue(createdBuildType);

        //подготовили комментарий
        String comment = BuildSteps.prepareCommentForCancellingBuild(testInfo.getDisplayName());

        CancelBuildRequest cancelBody = CancelBuildRequest.builder()
                .comment(comment)
                .readdIntoQueue(false)
                .build();

        CancelBuildResponse canceledBuild = new ValidatedCrudRequester<CancelBuildResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.CANCEL_BUILD,
                ResponseSpecs.requestReturnsOK()).post(cancelBody, createdBuild.getId());

        softly.assertThat(canceledBuild.getId()).isEqualTo(createdBuild.getId());
        softly.assertThat(canceledBuild.getState()).isEqualTo(FINISHED.getMessage());
        softly.assertThat(canceledBuild.getStatusText()).containsIgnoringCase(CANCELED.getMessage());
        softly.assertThat(canceledBuild.getBuildType().getId()).isEqualTo(createdBuildType);
        softly.assertThat(canceledBuild.getBuildType().getProjectId()).isEqualTo(createdProject.id);
        softly.assertThat(canceledBuild.getCanceledInfo()).isNotNull();
        softly.assertThat(canceledBuild.getCanceledInfo().getText()).isEqualTo(comment);

        //убедиться, что билд действительно отменен и его нет в очереди (статус finished)
        GetBuildResponse getBuildResponse = new ValidatedCrudRequester<GetBuildResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.GET_BUILD,
                ResponseSpecs.requestReturnsOK()).get(createdBuild.getId());
        softly.assertThat(getBuildResponse.getState()).isEqualTo(FINISHED.getMessage());
    }
}
