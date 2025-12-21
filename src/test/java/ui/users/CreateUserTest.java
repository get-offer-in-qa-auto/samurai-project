package ui.users;

import api.generators.RandomData;
import api.models.users.AuthUser;
import api.models.users.Roles;
import api.models.users.User;
import api.requests.steps.AdminSteps;
import common.annotations.WithAdminSession;
import common.annotations.WithAuthUser;
import common.messages.UserUiAlertMessage;
import common.extensions.AuthUserExtension;
import common.storage.UserSession;
import common.storage.UserSessionStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.users.CreateUserPage;
import ui.pages.users.UsersPage;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class CreateUserTest extends BaseUiTest {
    @Test
    @DisplayName("Успешное создание пользователя")
    @WithAdminSession
    public void successfullyCreateUser() {
        final String name = RandomData.getUserName();
        final String password = RandomData.getUserPassword();
        UsersPage usersPage = new CreateUserPage()
                .open()
                .createUserAndGoToUsers(name, password);

        UserSessionStore.registerUiUser(name, password);
        softly.assertThat(usersPage.getUsernamesFromTable())
                .as("Проверка юзера в таблице UI")
                .map(String::toLowerCase)
                .contains(name.toLowerCase());
        await()
                .atMost(5, SECONDS)
                .untilAsserted(() -> {
                    softly.assertThat(AdminSteps.getAllUsers())
                            .as("Проверка юзера в общем списке API")
                            .extracting(User::getUsername)
                            .map(String::toLowerCase)
                            .contains(name.toLowerCase());
                });
    }

    @Test
    @DisplayName("Неуспешное создание пользователя, пустое имя и пароль")
    @WithAdminSession
    public void unsuccessfullyCreateUserEmptyUsernameAndPassword() {
        List<User> beforeAddUser = AdminSteps.getAllUsers();
        new CreateUserPage()
                .open()
                .clickOnCreateButton();
        List<User> afterAddUser = AdminSteps.getAllUsers();


        assertUiErrorsMatchExactly(UserUiAlertMessage.USERNAME_EMPTY, UserUiAlertMessage.PASSWORD_EMPTY);
        softly.assertThat(beforeAddUser).as("Проверить что пользователь не создался")
                .isEqualTo(afterAddUser);
    }

    @Test
    @DisplayName("Неуспешное создание пользователя, незаполненное имя")
    @WithAdminSession
    public void unsuccessfullyCreateUserInvalidUsername() {
        List<User> beforeAddUser = AdminSteps.getAllUsers();
        final String password = RandomData.getUserPassword();
        new CreateUserPage()
                .open()
                .fillFieldsWithoutUsername(password);
        List<User> afterAddUser = AdminSteps.getAllUsers();


        assertUiErrorsMatchExactly(UserUiAlertMessage.USERNAME_EMPTY);
        softly.assertThat(beforeAddUser).as("Проверить что пользователь не создался")
                .isEqualTo(afterAddUser);
    }

    @Test
    @DisplayName("Неуспешное создание пользователя, неправильное подтверждение пароля")
    @WithAdminSession
    public void unsuccessfullyCreateUserInvalidConfirmPassword() {
        List<User> beforeAddUser = AdminSteps.getAllUsers();
        final String name = RandomData.getUserName();
        final String password = RandomData.getUserPassword();
        final String confirmPassword = RandomData.getUserPassword();
        new CreateUserPage()
                .open()
                .createUserWithConfirm(name, password, confirmPassword);
        List<User> afterAddUser = AdminSteps.getAllUsers();


        assertUiErrorsMatchExactly(UserUiAlertMessage.PASSWORD_MISMATCH);
        softly.assertThat(beforeAddUser).as("Проверить что пользователь не создался")
                .isEqualTo(afterAddUser);
    }

    @Test
    @DisplayName("Неуспешное создание пользователя,пользователь с таким именем уже существует")
    @WithAdminSession
    @WithAuthUser
    public void unsuccessfullyCreateUserWithSameUsername() {
        List<User> beforeAddUser = AdminSteps.getAllUsers();
        AuthUser authUser = AuthUserExtension.getAuthUser();
        final String password = RandomData.getUserPassword();
        new CreateUserPage()
                .open()
                .createUser(authUser.getUsername(), password);
        List<User> afterAddUser = AdminSteps.getAllUsers();


        assertUiErrorsContain(UserUiAlertMessage.USERNAME_DUPLICATE);
        softly.assertThat(beforeAddUser).as("Проверить что пользователь не создался")
                .isEqualTo(afterAddUser);
    }

    @Test
    @DisplayName("Успешная отмена cоздания пользователя")
    @WithAdminSession
    public void successfullyCancelCreateUser() {
        final String name = RandomData.getUserName();
        final String password = RandomData.getUserPassword();
        List<User> beforeAddUser = AdminSteps.getAllUsers();
        UsersPage usersPage = new CreateUserPage()
                .open()
                .cancelCreateAndGoToUsers(name, password);
        List<User> afterAddUser = AdminSteps.getAllUsers();

        softly.assertThat(usersPage.viewContentWrapper()).as("Проверка отображения главной страницы").isTrue();
        softly.assertThat(beforeAddUser).as("Проверить что пользователь не создался")
                .isEqualTo(afterAddUser);
    }
}