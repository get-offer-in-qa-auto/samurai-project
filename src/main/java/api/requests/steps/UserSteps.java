package api.requests.steps;

import api.generators.RandomData;
import api.generators.RandomModelGenerator;
import api.models.project.CreateProjectFromRepositoryRequest;
import api.models.project.CreateProjectManuallyRequest;
import api.models.project.CreateProjectResponse;
import api.models.users.CreateUserRequest;
import api.models.users.CreateUserTokenRequest;
import api.models.users.CreateUserTokenResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.specification.RequestSpecification;

public class UserSteps {
    public static CreateUserTokenResponse createTokenForUser(CreateUserRequest request) {
        String tokenName = RandomData.getTokenName();
        CreateUserTokenRequest userRequest = CreateUserTokenRequest.builder()
                .name(tokenName)
                .build();
        return new CrudRequester(
                RequestSpecs.userAuthSpecWithoutToken(request.getUsername(), request.getPassword()),
                Endpoint.USERS_CREATE_TOKEN,
                ResponseSpecs.ignoreErrors()
        ).post(userRequest, request.getId())
                .extract()
                .as(CreateUserTokenResponse.class);
    }

    public static CreateProjectResponse createProjectManually(RequestSpecification requestSpec) {
        return new ValidatedCrudRequester<CreateProjectResponse>(
                requestSpec,
                Endpoint.PROJECT_CREATE_MANUALLY,
                ResponseSpecs.requestReturnsOK())
                .post(RandomModelGenerator.generate(CreateProjectManuallyRequest.class));
    }

    public static CreateProjectResponse createProjectFromRepository(RequestSpecification requestSpec) {
        return new ValidatedCrudRequester<CreateProjectResponse>(
                requestSpec,
                Endpoint.PROJECT_CREATE_FROM_REPOSITORY,
                ResponseSpecs.requestReturnsOK())
                .post(RandomModelGenerator.generate(CreateProjectFromRepositoryRequest.class));
    }
}
