package ui.pages.agents.modalWindows;

import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import ui.pages.agents.AgentsPage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static ui.pages.agents.AgentStatus.DISABLE;

public class DisableModalWindow {
    private final SelenideElement modalWindowTitle = $("h2[class*='title']");
    private final SelenideElement modalWindowButtons = $("[class*='CommonForm-module__buttons']");
    private final SelenideElement submitButton = modalWindowButtons.$("[type='submit']");

    public DisableModalWindow disableModalWindowShouldHaveTitle(String agentName) {
        return StepLogger.log("Проверка заголовка модального окна выключения агента " + agentName, () -> {
        modalWindowTitle.shouldBe(visible).shouldHave(text(DISABLE.getMessage() + " " + agentName));
        return this;
        });
    }

    public AgentsPage clickConfirmButton() {
        return StepLogger.log("Подтверждение выключения агента", () -> {
        submitButton.shouldBe(visible).click();
        return new AgentsPage();
        });
    }
}
