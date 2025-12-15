package ui.pages.users;

import com.codeborne.selenide.SelenideElement;
import ui.pages.BasePage;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class CreateUserPage extends BasePage<CreateUserPage> {
    private final SelenideElement usernameField = $x("//input[@id='input_teamcityUsername']");
    private final SelenideElement nameField = $x("//input[@id='name']");
    private final SelenideElement emailField = $x("//input[@id='input_teamcityEmail']");
    private final SelenideElement newPasswordField = $x("//input[@id='password1']");
    private final SelenideElement confirmPasswordField = $x("//input[@id='retypedPassword']");
    private final SelenideElement createUserButton = $x("//input[@value='Create User']");
    private final SelenideElement cancelButton = $x("//div[@class='stayMessage']/following-sibling::div[1]/a[contains(text(),'Cancel')]");

    @Override
    public String url() {
        return "/admin/createUser.html";
    }

    public CreateUserPage createUser(String name, String password) {
        usernameField.shouldBe(visible, enabled).setValue(name);
        newPasswordField.shouldBe(visible, enabled).setValue(password);
        confirmPasswordField.shouldBe(visible, enabled).setValue(password);
        createUserButton.click();
        return this;
    }

    public CreateUserPage fillFieldsWithoutUsername(String password) {
        newPasswordField.shouldBe(visible, enabled).setValue(password);
        confirmPasswordField.shouldBe(visible, enabled).setValue(password);
        createUserButton.click();
        return this;
    }

    public UsersPage createUserAndGoToUsers(String name, String password) {
        createUser(name, password);
        return getPage(UsersPage.class);
    }
}