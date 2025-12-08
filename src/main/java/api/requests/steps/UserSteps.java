package api.requests.steps;

import api.generators.RandomData;
import api.models.users.CreateUserRequest;
import api.models.users.CreateUserResponse;
import api.models.users.CreateUserTokenRequest;
import api.models.users.CreateUserTokenResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

public class UserSteps {
    public static CreateUserTokenResponse createTokenForUser(CreateUserRequest request){
        String tokenName = RandomData.getTokenName();
        CreateUserTokenRequest userRequest = CreateUserTokenRequest.builder()
                .name(tokenName)
                .build();
        return new CrudRequester(
                RequestSpecs.userAuthSpecWithoutToken(request.getUsername(),request.getPassword()),
                Endpoint.USERS_CREATE_TOKEN,
                ResponseSpecs.ignoreErrors()
        ).post(userRequest,request.getId())
                .extract()
                .as(CreateUserTokenResponse.class);
    }
}
