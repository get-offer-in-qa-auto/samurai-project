package api.users;

import api.BaseTest;
import api.generators.RandomData;
import api.models.error.ErrorResponse;
import api.models.users.*;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.WithAuthUser;
import common.extensions.AuthUserExtension;
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

public class UpdateUserTest extends BaseTest {
    static Stream<Arguments> userCredentials() {
        return Stream.of(
                Arguments.of("serGiO", "ppas123"),
                Arguments.of("a_1!", "pPas_123!")
        );
    }

    @ParameterizedTest
    @DisplayName("Успешное редактирование пользователя")
    @MethodSource("userCredentials")
    @WithAuthUser
    public void successfulUpdateUserWithValidData(String username, String name) {
        AuthUser authUser = AuthUserExtension.getAuthUser();
        UpdateUserRequest request = UpdateUserRequest.builder()
                .username(username)
                .name(name)
                .build();
        UpdateUserResponse response = new ValidatedCrudRequester<UpdateUserResponse>(RequestSpecs.userAuthSpecWithToken(),
                Endpoint.USER_UPDATE, ResponseSpecs.requestReturnsOK())
                .put(request, authUser.getId());
        List<User> users = AdminSteps.getAllUsers();


        softly.assertThat(username.toLowerCase())
                .as("Проверка username в ответе")
                .isEqualTo(response.getUsername());
        softly.assertThat(name)
                .as("Проверка name в ответе")
                .isEqualTo(response.getName());
        softly.assertThat(users).as("Проверка юзера в общем списке")
                .extracting(User::getUsername, User::getName, User::getId)
                .containsOnlyOnce(tuple(response.getUsername().toLowerCase(), response.getName(), response.getId()));
    }

    @Test
    @DisplayName("Неуспешное редактирование пользователя, несуществующий id")
    public void unsuccessfullyUpdateUserWithInvalidId() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .username(RandomData.getUserName())
                .build();
        var response = new CrudRequester(RequestSpecs.adminAuthSpec(),
                Endpoint.USER_UPDATE, ResponseSpecs.requestReturns404NotFound())
                .put(request, RandomData.getId());
        List<User> users = AdminSteps.getAllUsers();
        ErrorResponse errorResponse = extractError(response);


        assertErrorMessage(errorResponse, UserErrorMessage.USER_NOT_FOUND);
        softly.assertThat(users).as("Проверить что пользователь не создался")
                .extracting(User::getUsername)
                .doesNotContain(request.getUsername());
    }

    @Test
    @DisplayName("Неуспешное редактирование пользователя, пустой username")
    @WithAuthUser
    public void unsuccessfullyUpdateUserWithEmptyUsername() {
        AuthUser authUser = AuthUserExtension.getAuthUser();
        UpdateUserRequest request = UpdateUserRequest.builder()
                .username("")
                .build();
        var response = new CrudRequester(RequestSpecs.userAuthSpecWithToken(),
                Endpoint.USER_UPDATE, ResponseSpecs.requestReturnsInternalServerError())
                .put(request, authUser.getId());
        List<User> users = AdminSteps.getAllUsers();
        ErrorResponse errorResponse = extractError(response);

        assertErrorMessage(errorResponse, UserErrorMessage.USERNAME_EMPTY_UPDATE);
        softly.assertThat(users).as("Проверка username в ответе")
                .extracting(User::getUsername)
                .containsOnlyOnce(authUser.getUsername().toLowerCase());
    }

    @Test
    @DisplayName("Неуспешное редактирование пользователя, пустой password")
    @WithAuthUser
    @Disabled("Ошибка, есть возможность отправить пустой пароль")
    public void unsuccessfullyUpdateUserWithEmptyPassword() {
        AuthUser authUser = AuthUserExtension.getAuthUser();
        UpdateUserRequest request = UpdateUserRequest.builder()
                .password("")
                .build();
        var response = new CrudRequester(RequestSpecs.userAuthSpecWithToken(),
                Endpoint.USER_UPDATE, ResponseSpecs.requestReturnsInternalServerError())
                .put(request, authUser.getId());
        List<User> users = AdminSteps.getAllUsers();
        ErrorResponse errorResponse = extractError(response);

        assertErrorMessage(errorResponse, UserErrorMessage.PASSWORD_EMPTY_UPDATE);
    }

    @Test
    @DisplayName("Неуспешное редактирование пользователя,такой username уже существует")
    @WithAuthUser
    public void unsuccessfullyUpdateUserWithSameUsername() {
        CreateUserRequest request = AdminSteps.createTemporaryUser();
        AuthUser authUser = AuthUserExtension.getAuthUser();
        UpdateUserRequest requestUpdate = UpdateUserRequest.builder()
                .username(request.getUsername())
                .build();
        var response = new CrudRequester(RequestSpecs.userAuthSpecWithToken(),
                Endpoint.USER_UPDATE, ResponseSpecs.requestReturnsInternalServerError())
                .put(requestUpdate, authUser.getId());
        List<User> users = AdminSteps.getAllUsers();
        ErrorResponse errorResponse = extractError(response);

        assertErrorMessageContains(errorResponse, UserErrorMessage.USERNAME_DUPLICATE_UPDATE);
        softly.assertThat(users).as("Проверка что username один")
                .extracting(User::getUsername)
                .containsOnlyOnce(authUser.getUsername().toLowerCase());
        AdminSteps.deleteUser(request);
    }
}