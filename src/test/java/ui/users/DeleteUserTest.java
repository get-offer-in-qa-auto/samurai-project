package ui.users;

import api.models.users.AuthUser;
import api.models.users.User;
import api.requests.steps.AdminSteps;
import common.annotations.WithAdminSession;
import common.annotations.WithAuthUser;
import common.messages.UserUiAlertMessage;
import common.extensions.AuthUserExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.users.CreateUserPage;
import ui.pages.users.UsersPage;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class DeleteUserTest extends BaseUiTest {

    @Test
    @DisplayName("Успешное удаление пользователя")
    @WithAdminSession
    @WithAuthUser
    public void successfullyDeleteUser() {
        AuthUser authUser = AuthUserExtension.getAuthUser();
        UsersPage usersPage = new UsersPage().open()
                .selectUserById(authUser.getId())
                .clickButtonRemove();

        checkAlertMessageAndAccept(UserUiAlertMessage.DELETE_ALERT);


        await()
                .atMost(5, SECONDS)
                .untilAsserted(() -> {
                    softly.assertThat(AdminSteps.getAllUsers())
                            .as("Проверка юзера в общем списке API")
                            .extracting(User::getUsername)
                            .map(String::toLowerCase)
                            .doesNotContain(authUser.getUsername().toLowerCase());
                });
    }
}