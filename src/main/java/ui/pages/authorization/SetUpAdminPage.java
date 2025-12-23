package ui.pages.authorization;

import com.codeborne.selenide.SelenideElement;
import ui.pages.BasePage;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;


public class SetUpAdminPage extends BasePage {
    private final SelenideElement username = $x("//input[@id='input_teamcityUsername']");
    private final SelenideElement header = $x("//input[@id='header']");

    @Override
    public String url() {
        return "/setupAdmin.html";
    }

    public SetUpAdminPage checkField() {
        username.shouldBe(visible, Duration.ofMinutes(5));
        return this;
    }
}
