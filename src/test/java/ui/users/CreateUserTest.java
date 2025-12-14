package ui.users;

import api.generators.RandomData;
import api.models.users.User;
import api.requests.steps.AdminSteps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.BasePage;
import ui.pages.users.CreateUserPage;

import java.util.List;

public class CreateUserTest extends BaseUiTest {
    @Test
    @DisplayName("Успешное создание пользователя")
    public void successfullyCreateUser() {
        final String name = RandomData.getUserName();
        final String password = RandomData.getUserPassword();
        BasePage.authAsSuperUser();
        new CreateUserPage().open().createUser(name, password);
        List<User> users = AdminSteps.getAllUsers();
        softly.assertThat(users).as("Проверка юзера в общем списке")
                .extracting(User::getUsername)
                .map(String::toLowerCase)
                .containsOnlyOnce(name.toLowerCase());
    }
}