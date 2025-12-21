package api.requests.steps;

import api.generators.RandomData;
import api.models.users.CreateUserRequest;
import api.models.users.CreateUserResponse;
import api.models.users.CreateUserRoleRequest;
import api.models.users.CreateUserRoleResponse;
import api.models.users.GetAllUsersResponse;
import api.models.users.GetUserRoleResponse;
import api.models.users.Roles;
import api.models.users.User;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.ResponseSpecs;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import java.util.List;

import static api.specs.RequestSpecs.adminAuthSpec;
import static io.restassured.RestAssured.given;

public class AdminSteps {

    public static CreateUserRequest createTemporaryUser() {
        String username = RandomData.getUserName();
        String password = RandomData.getUserPassword();

        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .build();

        CreateUserResponse userResponse = new CrudRequester(
                adminAuthSpec(),
                Endpoint.USER_CREATE,
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
                adminAuthSpec(),
                Endpoint.USER_CREATE_ROLE,
                ResponseSpecs.ignoreErrors())
                .post(addRoleRequest, request.getId())
                .extract()
                .as(CreateUserRoleRequest.class);
    }


    public static void deleteUser(CreateUserRequest request) {
        ValidatableResponse requestForDelete = new CrudRequester(
                adminAuthSpec(),
                Endpoint.USER_DELETE,
                ResponseSpecs.ignoreErrors())
                .delete(request.getId());
    }

    public static void deleteUser(CreateUserResponse response) {
        ValidatableResponse requestForDelete = new CrudRequester(
                adminAuthSpec(),
                Endpoint.USER_DELETE,
                ResponseSpecs.ignoreErrors())
                .delete(response.getId());
    }

    public static void deleteUser(String name) {
        List<User> users = AdminSteps.getAllUsers();
        int userId = users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(name))
                .mapToInt(User::getId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found: " + name));
        ValidatableResponse requestForDelete = new CrudRequester(
                adminAuthSpec(),
                Endpoint.USER_DELETE,
                ResponseSpecs.ignoreErrors())
                .delete(userId);
    }

    public static List<User> getAllUsers() {
        GetAllUsersResponse allUsersResponse = new ValidatedCrudRequester<GetAllUsersResponse>(adminAuthSpec(),
                Endpoint.GET_ALL_USERS, ResponseSpecs.requestReturnsOK())
                .get();
        return allUsersResponse.getUser();
    }

    public static List<CreateUserRoleResponse> getRoleUser(int userId) {
        GetUserRoleResponse getUserRoleResponse = new ValidatedCrudRequester<GetUserRoleResponse>(adminAuthSpec(),
                Endpoint.GET_USER_ROLE, ResponseSpecs.requestReturnsOK())
                .get(userId);
        return getUserRoleResponse.getRole();
    }

    public static String getAdminSessionId() {
        Response response = given()
                .spec(adminAuthSpec())
                .get("/users");
        String sessionId = response.getCookie("TCSESSIONID");
        if (sessionId == null) {
            throw new IllegalStateException("TCSESSIONID не найдена");
        }

        return sessionId;
    }
}
