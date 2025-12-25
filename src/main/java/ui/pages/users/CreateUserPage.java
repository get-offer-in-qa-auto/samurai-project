package ui.pages.users;

import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import ui.pages.BasePage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Selenide.$x;

public class CreateUserPage extends BasePage<CreateUserPage> {

    private final SelenideElement createUserButton = $x("//input[@value='Create User']");
    private final SelenideElement cancelButton = $x("//div[@class='stayMessage']/following-sibling::div[1]/a[contains(text(),'Cancel')]");

    @Override
    public String url() {
        return "/admin/createUser.html";
    }

    public CreateUserPage createUser(String name, String password) {
        return StepLogger.log("Создать пользователя", () -> {
            usernameField.shouldBe(visible, enabled).setValue(name);
            newPasswordField.shouldBe(visible, enabled).setValue(password);
            confirmPasswordField.shouldBe(visible, enabled).setValue(password);
            createUserButton.click();
            return this;
        });
    }

    public CreateUserPage createUserWithConfirm(String name, String password, String confirmPassword) {
        return StepLogger.log("Создать пользователя с подтверждением пароля", () -> {
            usernameField.shouldBe(visible, enabled).setValue(name);
            newPasswordField.shouldBe(visible, enabled).setValue(password);
            confirmPasswordField.shouldBe(visible, enabled).setValue(confirmPassword);
            createUserButton.click();
            return this;
        });
    }

    public CreateUserPage fillFieldsWithoutUsername(String password) {
        return StepLogger.log("Заполнить все поля кроме username", () -> {
            newPasswordField.shouldBe(visible, enabled).setValue(password);
            confirmPasswordField.shouldBe(visible, enabled).setValue(password);
            createUserButton.click();
            return this;
        });
    }

    public CreateUserPage clickOnCreateButton() {
        return StepLogger.log("Нажать на кнопку Create", () -> {
            createUserButton.click();
            return this;
        });
    }

    public UsersPage createUserAndGoToUsers(String name, String password) {
        return StepLogger.log("Создать пользователя и перейти на страницу всех пользователей", () -> {
            createUser(name, password);
            return getPage(UsersPage.class);
        });
    }

    public UsersPage cancelCreateAndGoToUsers(String name, String password) {
        return StepLogger.log("Отменить создание пользователя и перейти на страницу всех пользователей", () -> {
            fillRequiredFieldsAndClickCancel(name, password);
            return getPage(UsersPage.class);
        });
    }

    public CreateUserPage fillRequiredFieldsAndClickCancel(String name, String password) {
        return StepLogger.log("Заполнить обязательные поля и нажать на кнопку Cancel", () -> {
            usernameField.shouldBe(visible, enabled).setValue(name);
            newPasswordField.shouldBe(visible, enabled).setValue(password);
            confirmPasswordField.shouldBe(visible, enabled).setValue(password);
            cancelButton.click();
            return this;
        });
    }
}
