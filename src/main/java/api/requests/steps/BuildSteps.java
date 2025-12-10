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
    public static CreateBuildRequest addBuildToQueue(BuildType buildType){
        CreateBuildRequest build = CreateBuildRequest.builder()
                .buildType(
                        CreateBuildRequest.BuildType.builder()
                                .id(buildType.getId())
                                .build()
                )
                .build();

        new ValidatedCrudRequester<CreateBuildResponse>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.entityWasCreated()).post(build);

        return build;
    }


    public static GetBuildResponse getBuildFromQueue(CreateBuildResponse response){
        GetBuildResponse getBuildResponse = new ValidatedCrudRequester<GetBuildResponse>(
                    RequestSpecs.adminAuthSpec(),
                    Endpoint.GET_BUILD,
                    ResponseSpecs.requestReturnsOK()).get(response.getId());
            return getBuildResponse;

    }

    public static void deleteBuildFromQueue(CreateBuildResponse response){
        new ValidatedCrudRequester<BaseModel>(
        RequestSpecs.adminAuthSpec(),
                Endpoint.DELETE_BUILD_FROM_QUEUE,
                ResponseSpecs.requestReturnsNoContent()).delete(response.getId());
    }

    public static void cancelBuild(CreateBuildResponse response){
        CancelBuildRequest cancelBody = CancelBuildRequest.builder()
                .comment("Cancelled by automated test")
                .readdIntoQueue(false)
                .build();

        new ValidatedCrudRequester<BaseModel>(
                RequestSpecs.adminAuthSpec(),
                Endpoint.CANCEL_BUILD,
                ResponseSpecs.requestReturnsOK()).post(response, response.getId());
    }
}
