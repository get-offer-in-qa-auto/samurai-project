package ui.pages.agents.modalWindows;

import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
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
        return StepLogger.log("Проверка заголовка модального окна деавторизации агента " + agentName, () -> {
            modalWindowTitle.shouldBe(visible).shouldHave(text(UNAUTHORIZE.getMessage() + " " + agentName));
            return this;
        });
    }

    public AgentsPage clickConfirmButton() {
        return StepLogger.log("Подтверждение деавторизации агента", () -> {
            AgentsPage agentsPage = new AgentsPage();
            submitButton.shouldBe(visible).click();
            return agentsPage;
        });
    }
}
