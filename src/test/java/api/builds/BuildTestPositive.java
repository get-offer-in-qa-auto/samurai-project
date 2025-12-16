package api.builds;

import api.BaseTest;
import api.comparison.ModelAssertions;
import api.models.BaseModel;
import api.models.builds.*;
import api.models.users.Roles;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.BuildSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.WithAuthUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class BuildTestPositive extends BaseTest {

    @Test
   @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanCreateBuild(){
        //создадим проект и buildType
        String createdBuildType = BuildSteps.setEnvironmentToCreateBuild();

        //тело запроса
        CreateBuildRequest build = CreateBuildRequest.builder()
                .buildType(
                        CreateBuildRequest.BuildType.builder()
                                .id(createdBuildType)
                                .build()
                )
                .build();

        //постановка билда в очередь
        CreateBuildResponse createdBuild = new ValidatedCrudRequester<CreateBuildResponse>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.entityWasCreated()).post(build);
        ModelAssertions.assertThatModels(build, createdBuild).match();

        //узнает id проекта, чтобы его удалить
        String projectId = createdBuild.getBuildType().getProjectId();

        //проверим, что такой билд действительно появился
        GetBuildResponse getBuildResponse = BuildSteps.getBuildFromQueue(createdBuild);
        softly.assertThat(getBuildResponse.getBuildTypeId()).isEqualTo(createdBuild.getBuildType().getId());
        softly.assertThat(getBuildResponse.getId()).isEqualTo(createdBuild.getId());

        //удалим проект и все его содержимое
        BuildSteps.cleanEnvironmentAfterBuilds(projectId,RequestSpecs.userAuthSpecWithToken());
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanGetBuild(){
        //создадим проект и buildType
        String createdBuildType = BuildSteps.setEnvironmentToCreateBuild();

        //создать билд
        CreateBuildResponse createdBuild = BuildSteps.addBuildToQueue(createdBuildType);
        Integer buildId = createdBuild.getId();

        //вернуть билд по его id
        GetBuildResponse getBuildResponse = new ValidatedCrudRequester<GetBuildResponse>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.GET_BUILD,
                ResponseSpecs.requestReturnsOK()).get(buildId);
        ModelAssertions.assertThatModels(createdBuild, getBuildResponse).match();

        softly.assertThat(getBuildResponse.getBuildTypeId()).isEqualTo(createdBuild.getBuildType().getId());
        softly.assertThat(getBuildResponse.getId()).isEqualTo(createdBuild.getId());

        //узнает id проекта, чтобы его удалить
        String projectId = createdBuild.getBuildType().getProjectId();

        //удалим проект и все его содержимое
        BuildSteps.cleanEnvironmentAfterBuilds(projectId,RequestSpecs.userAuthSpecWithToken());
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanDeleteBuild(){
        //создадим проект и buildType
        String createdBuildType = BuildSteps.setEnvironmentToCreateBuild();

        //создать билд
        CreateBuildResponse createdBuild = BuildSteps.addBuildToQueue(createdBuildType);
        Integer buildId = createdBuild.getId();

        //узнает id проекта, чтобы его удалить
        String projectId = createdBuild.getBuildType().getProjectId();

        new ValidatedCrudRequester<BaseModel>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.DELETE_BUILD_FROM_QUEUE,
                ResponseSpecs.requestReturnsNoContent()).delete(buildId);

        //убедиться, что билд удален
        BuildSteps.checkIfBuildIsDeleted(createdBuild);


        //удалим проект и все его содержимое
        BuildSteps.cleanEnvironmentAfterBuilds(projectId,RequestSpecs.userAuthSpecWithToken());

    }

    @Test
    @DisplayName("Cancel build from queue via API")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanCancelBuild(TestInfo testInfo){
        //создадим проект и buildType
        String createdBuildType = BuildSteps.setEnvironmentToCreateBuild();

        //создать билд
        CreateBuildResponse createdBuild = BuildSteps.addBuildToQueue(createdBuildType);
        Integer buildId = createdBuild.getId();

        //узнает id проекта, чтобы его удалить
        String projectId = createdBuild.getBuildType().getProjectId();

        //подготовили комментарий
        String comment = BuildSteps.prepareCommentForCancellingBuild(testInfo.getDisplayName());

        CancelBuildRequest cancelBody = CancelBuildRequest.builder()
                .comment(comment)
                .readdIntoQueue(false)
                .build();

        CancelBuildResponse canceledBuild = new ValidatedCrudRequester<CancelBuildResponse>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.CANCEL_BUILD,
                ResponseSpecs.requestReturnsOK()).post(createdBuild, createdBuild.getId());

        ModelAssertions.assertThatModels(createdBuild, canceledBuild).match();
        softly.assertThat(canceledBuild.getState()).isEqualTo("finished");

        //убедиться, что билд действительно отменен
        BuildSteps.checkIfBuildIsCancelled(createdBuild);

        //удалим проект и все его содержимое
        BuildSteps.cleanEnvironmentAfterBuilds(projectId,RequestSpecs.userAuthSpecWithToken());

    }
}
