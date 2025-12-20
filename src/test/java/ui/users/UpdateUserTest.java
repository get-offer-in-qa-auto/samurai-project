package ui.users;

import api.generators.RandomData;
import api.models.users.AuthUser;
import api.models.users.User;
import api.requests.steps.AdminSteps;
import common.annotations.WithAuthUser;
import common.messages.UserUiAlertMessage;
import common.extensions.AuthUserExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.users.UserProfilePage;

import java.util.Objects;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class UpdateUserTest extends BaseUiTest {

    @Test
    @WithAuthUser
    @DisplayName("Успешное изменение имени пользователя")
    public void successfullyChangeName() {
        AuthUser authUser = AuthUserExtension.getAuthUser();
        final String newName = RandomData.getUserName();
        UserProfilePage profile = new UserProfilePage()
                .open()
                .fillName(newName)
                .pressSaveButton();
        softly.assertThat(profile.viewSuccessMessage(UserUiAlertMessage.SUCCESS_CHANGES))
                .as("Отображение сообщения успешного изменения профиля")
                .isTrue();

        User updatedUser = await()
                .atMost(5, SECONDS)
                .until(() -> AdminSteps.getAllUsers().stream()
                                .filter(u -> u.getUsername().equalsIgnoreCase(authUser.getUsername()))
                                .findFirst()
                                .orElse(null),
                        Objects::nonNull
                );

        softly.assertThat(updatedUser.getName())
                .as("Проверка изменения имени пользователя")
                .isEqualTo(newName);

    }

    @Test
    @WithAuthUser
    @DisplayName("Успешное изменение пароля пользователя")
    public void successfullyChangePassword() {
        AuthUser authUser = AuthUserExtension.getAuthUser();
        final String password = RandomData.getUserPassword();
        UserProfilePage profile = new UserProfilePage()
                .open()
                .fillCurrentPassword(authUser.getPassword())
                .fillNewPassword(password)
                .fillConfirmPassword(password)
                .pressSaveButton();
        softly.assertThat(profile.viewSuccessMessage(UserUiAlertMessage.SUCCESS_CHANGES))
                .as("Отображение сообщения успешного изменения профиля")
                .isTrue();
    }

    @Test
    @WithAuthUser
    @DisplayName("Неуспешное изменение пароля пользователя,введен неверный текущий пароль")
    public void unsuccessfullyChangePasswordInvalidCurrentPassword() {
        AuthUser authUser = AuthUserExtension.getAuthUser();
        final String password = RandomData.getUserPassword();
        new UserProfilePage()
                .open()
                .fillCurrentPassword(password)
                .fillNewPassword(password)
                .fillConfirmPassword(password)
                .pressSaveButton();

        assertUiErrorsContain(UserUiAlertMessage.CURRENT_PASSWORD_INCORRECT);
    }

    @Test
    @WithAuthUser
    @DisplayName("Неуспешное изменение пароля пользователя,подтверждающий пароль отличается от нового")
    public void unsuccessfullyChangePasswordInvalidConfirmPassword() {
        AuthUser authUser = AuthUserExtension.getAuthUser();
        final String password = RandomData.getUserPassword();
        final String confirmPassword = RandomData.getUserPassword();
        new UserProfilePage()
                .open()
                .fillCurrentPassword(authUser.getPassword())
                .fillNewPassword(password)
                .fillConfirmPassword(confirmPassword)
                .pressSaveButton();

        assertUiErrorsContain(UserUiAlertMessage.PASSWORD_MISMATCH);
    }
}