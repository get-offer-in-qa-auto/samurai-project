package api.users;

import api.BaseTest;
import api.generators.RandomData;
import api.models.error.ErrorResponse;
import api.models.users.*;
import api.models.users.AuthUser;
import api.models.users.CreateUserRequest;
import api.models.users.CreateUserResponse;
import api.models.users.User;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.WithAuthUser;
import common.extensions.AuthUserExtension;
import common.storage.UserSession;
import common.storage.UserSessionStore;
import common.messages.UserErrorMessage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.tuple;

public class CreateUserTest extends BaseTest {

    static Stream<Arguments> userCredentials() {
        return Stream.of(
                Arguments.of("serGiO", "pPas123"),
                Arguments.of("a_1!", "pP as_123!")
        );
    }

    @ParameterizedTest
    @DisplayName("Успешное создание пользователя")
    @MethodSource("userCredentials")
    public void successfulCreationUserWithValidData(String username, String password) {
        UserSession user = UserSessionStore.create(username, password, Roles.USER_ROLE);
        List<User> users = AdminSteps.getAllUsers();

        softly.assertThat(username).as("Проверка username в ответе").isEqualTo(user.getAuthUser().getUsername());
        softly.assertThat(users).as("Проверка юзера в общем списке")
                .extracting(u -> u.getUsername().toLowerCase(),
                        User::getId)
                .containsOnlyOnce(tuple(user.getAuthUser().getUsername().toLowerCase(), user.getAuthUser().getId()));
    }

    @DisplayName("Неуспешное создание пользователя, одинаковое username")
    @Test
    @WithAuthUser
    public void unsuccessfullyCreationUserWithSameUserName() {
        AuthUser authUser = AuthUserExtension.getAuthUser();
        CreateUserRequest request = CreateUserRequest.builder()
                .username(authUser.getUsername())
                .password(RandomData.getUserPassword())
                .build();
        var response = new CrudRequester(RequestSpecs.adminAuthSpec(),
                Endpoint.USER_CREATE, ResponseSpecs.requestReturns400BadRequest())
                .post(request);
        List<User> users = AdminSteps.getAllUsers();
        ErrorResponse errorResponse = extractError(response);


        assertErrorMessage(errorResponse, UserErrorMessage.USERNAME_DUPLICATE);
        softly.assertThat(users).as("Проверка что не создался дублирующий юзер")
                .extracting(User::getUsername)
                .containsOnlyOnce(authUser.getUsername().toLowerCase());
    }

    @DisplayName("Неуспешное создание пользователя, не заполнено поле username")
    @Test
    public void unsuccessfullyCreationUserWithEmptyUsername() {
        List<User> beforeCreation = AdminSteps.getAllUsers();
        CreateUserRequest request = CreateUserRequest.builder()
                .password(RandomData.getUserPassword())
                .build();
        var response = new CrudRequester(RequestSpecs.adminAuthSpec(),
                Endpoint.USER_CREATE, ResponseSpecs.requestReturns400BadRequest())
                .post(request);
        List<User> afterCreation = AdminSteps.getAllUsers();
        ErrorResponse errorResponse = extractError(response);


        assertErrorMessage(errorResponse, UserErrorMessage.USERNAME_EMPTY);
        softly.assertThat(beforeCreation).as("Проверить что пользователь не создался")
                .isEqualTo(afterCreation);
    }

    @DisplayName("Неуспешное создание пользователя, не заполнено поле password")
    @Test
    @Disabled("Ошибка, пользователь создается")
    public void unsuccessfullyCreationUserWithEmptyPassword() {
        List<User> beforeCreation = AdminSteps.getAllUsers();
        CreateUserRequest request = CreateUserRequest.builder()
                .username(RandomData.getUserName())
                .build();
        var response = new CrudRequester(RequestSpecs.adminAuthSpec(),
                Endpoint.USER_CREATE, ResponseSpecs.requestReturns400BadRequest())
                .post(request);
        List<User> afterCreation = AdminSteps.getAllUsers();
        ErrorResponse errorResponse = extractError(response);


        assertErrorMessage(errorResponse, UserErrorMessage.PASSWORD_EMPTY);
        softly.assertThat(beforeCreation).as("Проверить что пользователь не создался")
                .isEqualTo(afterCreation);
    }
}