package api.users;

import api.BaseTest;
import api.generators.RandomData;
import api.models.error.ErrorResponse;
import api.models.users.CreateUserRequest;
import api.models.users.User;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.messages.UserErrorMessage;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DeleteUserTest extends BaseTest {


    @Test
    @DisplayName("Успешное удаление пользователя")
    public void successfullyDeleteUser() {
        CreateUserRequest user = AdminSteps.createTemporaryUser();
        ValidatableResponse response = new CrudRequester(RequestSpecs.adminAuthSpec(),
                Endpoint.USER_DELETE, ResponseSpecs.ignoreErrors())
                .delete(user.getId());
        List<User> users = AdminSteps.getAllUsers();


        softly.assertThat(users).as("Проверить что пользователь не создался")
                .extracting(User::getUsername)
                .doesNotContain(user.getUsername());

    }

    @Test
    @DisplayName("Неуспешное удаление пользователя, несуществующий id")
    public void unsuccessfullyDeleteUserWithInvalidId() {
        List<User> beforeCreation = AdminSteps.getAllUsers();
        ValidatableResponse response = new CrudRequester(RequestSpecs.adminAuthSpec(),
                Endpoint.USER_DELETE, ResponseSpecs.requestReturns404NotFound())
                .delete(RandomData.getId());
        List<User> afterCreation = AdminSteps.getAllUsers();
        ErrorResponse errorResponse = extractError(response);


        assertErrorMessage(errorResponse, UserErrorMessage.USER_NOT_FOUND);
        softly.assertThat(beforeCreation).as("Проверить что пользователь не создался")
                .isEqualTo(afterCreation);
    }
}