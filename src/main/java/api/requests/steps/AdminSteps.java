package api.requests.steps;

import api.generators.RandomData;
import api.models.users.CreateUserRequest;
import api.models.users.CreateUserResponse;
import api.models.users.CreateUserRoleRequest;
import api.models.users.Roles;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.response.ValidatableResponse;

public class AdminSteps {

    public static CreateUserRequest createTemporaryUser() {
        String username = RandomData.getUserName();
        String password = RandomData.getUserPassword();

        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .build();

        CreateUserResponse userResponse = new CrudRequester(
                RequestSpecs.adminAuthSpec(),
                Endpoint.USERS_CREATE,
                ResponseSpecs.ignoreErrors()
        ).post(userRequest)
                .extract()
                .as(CreateUserResponse.class);

        /**
         * Сохраняем id для дальнейшего использования
         */
        userRequest.setId(userResponse.getId());

        return userRequest;
    }

    public static CreateUserRoleRequest addRoleForUser(CreateUserRequest request, Roles role) {
        CreateUserRoleRequest addRoleRequest = CreateUserRoleRequest.builder()
                .roleId(role.name())
                .scope("p:_Root")
                .build();


        return new CrudRequester(
                RequestSpecs.adminAuthSpec(),
                Endpoint.USERS_CREATE_ROLE,
                ResponseSpecs.ignoreErrors())
                .post(addRoleRequest, request.getId())
                .extract()
                .as(CreateUserRoleRequest.class);
    }


    public  static  void deleteUser(CreateUserRequest request){
        ValidatableResponse requestForDelete = new CrudRequester(
                RequestSpecs.adminAuthSpec(),
                Endpoint.USERS_DELETE,
                ResponseSpecs.ignoreErrors())
                .delete(request.getId());
    }




}
