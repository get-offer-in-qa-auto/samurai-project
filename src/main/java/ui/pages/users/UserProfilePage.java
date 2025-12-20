package ui.pages.users;

import com.codeborne.selenide.SelenideElement;
import common.messages.UserUiAlertMessage;
import ui.pages.BasePage;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class UserProfilePage extends BasePage<UserProfilePage> {
    private final SelenideElement currentPassword = $x("//input[@id='currentPassword']");
    private final SelenideElement buttonSaveChanges = $x("//input[@value='Save changes']");
    private final SelenideElement successChangeMessage = $x("//div[@id='message_userChanged']");

    @Override
    public String url() {
        return "/profile.html";
    }


    public UserProfilePage fillName(String name) {
        nameField.shouldBe(visible, enabled).setValue(name);
        return this;
    }

    public UserProfilePage fillEmail(String email) {
        emailField.shouldBe(visible, enabled).setValue(email);
        return this;
    }

    public UserProfilePage fillCurrentPassword(String password) {
        currentPassword.shouldBe(visible, enabled).setValue(password);
        return this;
    }

    public UserProfilePage fillNewPassword(String password) {
        newPasswordField.shouldBe(visible, enabled).setValue(password);
        return this;
    }

    public UserProfilePage fillConfirmPassword(String password) {
        confirmPasswordField.shouldBe(visible, enabled).setValue(password);
        return this;
    }

    public UserProfilePage pressSaveButton() {
        buttonSaveChanges.shouldBe(visible, enabled).scrollTo().click();
        return this;
    }

    public boolean viewSuccessMessage(UserUiAlertMessage message) {
        return successChangeMessage.scrollTo().shouldBe(visible, enabled).isDisplayed();
    }
}