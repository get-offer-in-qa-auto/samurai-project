package ui.pages.agents.modalWindows;

import com.codeborne.selenide.SelenideElement;
import ui.pages.agents.AgentsPage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static ui.pages.agents.AgentStatus.UNAUTHORIZE;

public class UnAuthorizeModalWindow {
    private final SelenideElement modalWindowTitle = $("h2[class*='title']");
    private final SelenideElement modalWindowButtons = $("[class*='CommonForm-module__buttons']");
    private final SelenideElement submitButton = modalWindowButtons.$("[type='submit']");

    public UnAuthorizeModalWindow unAuthoriseModalWindowShouldHaveTitle(String agentName) {
        modalWindowTitle.shouldBe(visible).shouldHave(text(UNAUTHORIZE.getMessage() + " " + agentName));
        return this;
    }

    public AgentsPage clickConfirmButton() {
        AgentsPage agentsPage = new AgentsPage();
        submitButton.shouldBe(visible).click();
        return agentsPage;
    }
}
