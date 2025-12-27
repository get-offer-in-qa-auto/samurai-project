package api.requests.steps;

import api.models.BaseModel;
import api.models.buildConfiguration.BuildType;
import api.models.builds.BuildQueueResponse;
import api.models.builds.CancelBuildRequest;
import api.models.builds.CreateBuildRequest;
import api.models.builds.CreateBuildResponse;
import api.models.builds.GetBuildResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.helpers.StepLogger;

import static common.states.BuildState.FINISHED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class BuildSteps {
    public static CreateBuildResponse addBuildToQueue(String buildTypeId) {

        return StepLogger.log("Админ добавляет билд в очередь по buildTypeId: " + buildTypeId, ()-> {
        CreateBuildRequest build = CreateBuildRequest.builder()
                .buildType(
                      BuildType.builder()
                              .id(buildTypeId)
                             .build()
              )
              .build();
         CreateBuildResponse createdBuild = new ValidatedCrudRequester<CreateBuildResponse>(
            RequestSpecs.adminAuthSpec(),
            Endpoint.BUILD_QUEUE,
            ResponseSpecs.requestReturnsOK()).post(build);
         return createdBuild;
        });
    }

    public static GetBuildResponse getBuildFromQueue(CreateBuildResponse response) {
        return StepLogger.log("Админ возвращает билд " + response.getId() + " из очереди" , ()-> {
            GetBuildResponse getBuildResponse = new ValidatedCrudRequester<GetBuildResponse>(
                    RequestSpecs.userAuthSpecWithToken(),
                    Endpoint.GET_BUILD,
                    ResponseSpecs.requestReturnsOK()).get(response.getId());
            return getBuildResponse;
                });
    }

    public static void checkIfBuildHasAlreadyDeleted(CreateBuildResponse response) {
        StepLogger.log("Админ проверяет, что билд " + response.getId() + " удален", () -> {
            new CrudRequester(
                    RequestSpecs.userAuthSpecWithToken(),
                    Endpoint.GET_BUILD,
                    ResponseSpecs.requestReturns404NotFound()).get(response.getId());
        });
    }

    public static void deleteBuildFromQueue(CreateBuildResponse response) {
        StepLogger.log("Админ удаляет билд : " + response.getId() + "из очереди", ()-> {
            new CrudRequester(
                    RequestSpecs.userAuthSpecWithToken(),
                    Endpoint.DELETE_BUILD_FROM_QUEUE,
                    ResponseSpecs.requestReturnsNoContent()).delete(response.getId());
        });
    }

    public static String prepareCommentForCancellingBuild(String testName) {
        return StepLogger.log("Админ написал комментарий для удаления билда ", ()-> {
            return String.format(
                    "Canceled by API test [%s] at %s",
                    testName,
                    java.time.LocalDateTime.now()
            );
        });
    }

    public static BuildQueueResponse getBuildQueue() {
        return StepLogger.log("Админ возвращает всю очередь билдов ", ()-> {
            return new ValidatedCrudRequester<BuildQueueResponse>(
                    RequestSpecs.userAuthSpecWithToken(),
                    Endpoint.GET_BUILDS_QUEUE,
                    ResponseSpecs.requestReturnsOK()
            ).get();
        });
    }

    public static void cancelBuild(CreateBuildResponse response) {
        StepLogger.log("Админ отменяет билд: " + response.getId(), ()-> {
            String comment = "Cancelled by automated test";
            CancelBuildRequest cancelBody = CancelBuildRequest.builder()
                    .comment(comment)
                    .readdIntoQueue(false)
                    .build();

            new ValidatedCrudRequester<BaseModel>(
                    RequestSpecs.adminAuthSpec(),
                    Endpoint.CANCEL_BUILD,
                    ResponseSpecs.requestReturnsOK()).post(response, response.getId());
        });
    }
    public static void checkIfBuildIsAlreadyCanceled(CreateBuildResponse createdBuild){
         StepLogger.log("Админ проверяет, что билд: " + createdBuild.getId() + " отменен", ()-> {
             //убедиться, что билд действительно отменен и его нет в очереди (статус finished)
             GetBuildResponse getBuildResponse = new ValidatedCrudRequester<GetBuildResponse>(
                     RequestSpecs.userAuthSpecWithToken(),
                     Endpoint.GET_BUILD,
                     ResponseSpecs.requestReturnsOK()).get(createdBuild.getId());

             assertThat(getBuildResponse.getState()).isEqualTo(FINISHED.getMessage());
         });
    }
}
