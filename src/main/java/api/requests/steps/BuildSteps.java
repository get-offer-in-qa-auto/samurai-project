package api.requests.steps;

import api.models.BaseModel;
import api.models.buildConfiguration.BuildType;
import api.models.builds.CancelBuildRequest;
import api.models.builds.CreateBuildRequest;
import api.models.builds.CreateBuildResponse;
import api.models.builds.GetBuildResponse;
import api.models.project.CreateProjectResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.specification.RequestSpecification;

public class BuildSteps {
    public static CreateBuildResponse addBuildToQueue(String createdBuildType){
        CreateBuildRequest build = CreateBuildRequest.builder()
                .buildType(
                        CreateBuildRequest.BuildType.builder()
                                .id(createdBuildType)
                                .build()
                )
                .build();
        CreateBuildResponse response = new ValidatedCrudRequester<CreateBuildResponse>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.entityWasCreated()).post(build);
        return response;
    }

    public static GetBuildResponse getBuildFromQueue(CreateBuildResponse response){
        GetBuildResponse getBuildResponse = new ValidatedCrudRequester<GetBuildResponse>(
                    RequestSpecs.adminAuthSpec(),
                    Endpoint.GET_BUILD,
                    ResponseSpecs.requestReturnsOK()).get(response.getId());
            return getBuildResponse;

    }

    public static void checkIfBuildIsDeleted(CreateBuildResponse response){
        GetBuildResponse getBuildResponse = new ValidatedCrudRequester<GetBuildResponse>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.GET_BUILD,
                ResponseSpecs.requestReturns404NotFound()).get(response.getId());
    }

    public static void checkIfBuildIsCancelled(CreateBuildResponse response){
        GetBuildResponse getBuildResponse = new ValidatedCrudRequester<GetBuildResponse>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.GET_BUILD,
                ResponseSpecs.requestReturnsOK()).get(response.getId());
    }



    public static void deleteBuildFromQueue(CreateBuildResponse response){
        new ValidatedCrudRequester<BaseModel>(
        RequestSpecs.adminAuthSpec(),
                Endpoint.DELETE_BUILD_FROM_QUEUE,
                ResponseSpecs.requestReturnsNoContent()).delete(response.getId());
    }

    public static String prepareCommentForCancellingBuild(String testName){
        return String.format(
                "Canceled by API test [%s] at %s",
                testName,
                java.time.LocalDateTime.now()
        );
    }

    public static void cancelBuild(CreateBuildResponse response){
        String comment = "Cancelled by automated test";
        CancelBuildRequest cancelBody = CancelBuildRequest.builder()
                .comment(comment)
                .readdIntoQueue(false)
                .build();

        new ValidatedCrudRequester<BaseModel>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.CANCEL_BUILD,
                ResponseSpecs.requestReturnsOK()).post(response, response.getId());
    }

    public static String setEnvironmentToCreateBuild(){
        //создать проект
        CreateProjectResponse createdProject = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());

        //создать buildType в нем
        String createdBuildType = UserSteps.createBuildType(createdProject.getId(),RequestSpecs.userAuthSpecWithToken());

        return createdBuildType;
    }

    public static void cleanEnvironmentAfterBuilds(String projectId, RequestSpecification requestSpec){
        new CrudRequester(
                requestSpec,
                Endpoint.PROJECT_DELETE,
                ResponseSpecs.ignoreErrors())
                .delete(projectId);
    }



}
