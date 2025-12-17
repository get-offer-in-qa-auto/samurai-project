package api.requests.steps;

import api.models.BaseModel;
import api.models.buildconfiguration.BuildType;
import api.models.builds.CancelBuildRequest;
import api.models.builds.CreateBuildRequest;
import api.models.builds.CreateBuildResponse;
import api.models.builds.GetBuildResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;


public class BuildSteps {
    public static CreateBuildResponse addBuildToQueue(String createdBuildType){
        CreateBuildRequest build = CreateBuildRequest.builder()
                .buildType(
                        BuildType.builder()
                                .id(createdBuildType)
                                .build()
                )
                .build();

        //постановка билда в очередь
        CreateBuildResponse createdBuild = new ValidatedCrudRequester<CreateBuildResponse>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.requestReturnsOK()).post(build);
        return createdBuild;
    }

    public static GetBuildResponse getBuildFromQueue(CreateBuildResponse response){
        GetBuildResponse getBuildResponse = new ValidatedCrudRequester<GetBuildResponse>(
                    RequestSpecs.userAuthSpecWithToken(),
                    Endpoint.GET_BUILD,
                    ResponseSpecs.requestReturnsOK()).get(response.getId());
            return getBuildResponse;

    }

    public static void checkIfBuildIsDeleted(CreateBuildResponse response){
        GetBuildResponse getBuildResponse = new ValidatedCrudRequester<GetBuildResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.GET_BUILD,
                ResponseSpecs.requestReturns404NotFound()).get(response.getId());
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
}
