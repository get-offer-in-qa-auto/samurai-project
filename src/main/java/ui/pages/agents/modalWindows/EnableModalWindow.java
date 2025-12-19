package ui.pages.agents.modalWindows;

import com.codeborne.selenide.SelenideElement;
import ui.pages.agents.AgentsPage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static ui.pages.agents.AgentStatus.ENABLE;

public class EnableModalWindow {
    private final SelenideElement modalWindowTitle = $("h2[class*='title']");
    private final SelenideElement modalWindowButtons = $("[class*='CommonForm-module__buttons']");
    private final SelenideElement commentModule = $("[class*='EnableAgentForm-module__comment']");
    private final SelenideElement inputCommentField = commentModule.$("textarea");
    private final SelenideElement submitButton = modalWindowButtons.$("[type='submit']");

    public EnableModalWindow setEnableModalWindowShouldHaveTitle(String agentName) {
        modalWindowTitle.shouldBe(visible).shouldHave(text(ENABLE.getMessage() + " " + agentName));
        return this;
    }

    public EnableModalWindow setComment(String comment) {
        commentModule.shouldBe(visible).shouldHave(text("Comment"));
        inputCommentField.shouldBe(visible).setValue(comment);
        return this;
    }

    public AgentsPage clickConfirmButton() {
        AgentsPage agentsPage = new AgentsPage();
        submitButton.shouldBe(visible).click();
        return agentsPage;
    }
}
