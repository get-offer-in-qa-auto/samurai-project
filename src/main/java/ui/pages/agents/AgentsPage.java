package ui.pages.agents;

import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import ui.pages.BasePage;
import ui.pages.agents.modalWindows.AuthorizeModalWindow;
import ui.pages.agents.modalWindows.DisableModalWindow;
import ui.pages.agents.modalWindows.EnableModalWindow;
import ui.pages.agents.modalWindows.UnAuthorizeModalWindow;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class AgentsPage extends BasePage<AgentsPage> {
    @Override
    public String url() {
        return "/agents/overview";
    }

    private final SelenideElement agentsPageTitle = $("h1[class*='PageTitle-module__title']");
    private final SelenideElement agentPageTitle = $("h1[class*='AgentPage-module__title']");
    private final SelenideElement sidebarToggle = $("[ data-hint-container-id ='toggle-sidebar']");
    private final SelenideElement agentPageStatus = $("[class*='AgentPage-module__status']");
    private final SelenideElement agentConnectionStatus = agentPageStatus.$("[data-test-agent-connection-status='true']");
    private final SelenideElement agentAuthorizationStatus = agentPageStatus.$("[data-agent-authorization-status='true']");
    private final SelenideElement agentAuthorizationStatusComment = agentAuthorizationStatus.$("[class*='AgentStatus-module__comment']");
    private final SelenideElement agentEnabledStatus = agentPageStatus.$("[data-agent-enabled-status='true']");
    private final SelenideElement agentEnabledStatusComment = agentEnabledStatus.$("[class*='AgentStatus-module__comment']");
    private final SelenideElement setEnabledStatusButton = $("[data-test-enable-agent='true']").$("button");
    private final SelenideElement setAuthorizationStatusButton = $("button[data-test-authorize-agent='true']");

    public AgentsPage agentsPageShouldBeOpen() {
        return StepLogger.log("Проверка, что страница агентов открыта", () -> {
            agentsPageTitle.shouldBe(visible).shouldHave(text("Overview"));
            return this;
        });
    }

    public AgentsPage agentPageTitleShouldHaveName(String agentName) {
        return StepLogger.log("Проверка отображения имени агента в заголовке страницы: " + agentName, () -> {
            agentPageTitle.shouldBe(visible).shouldHave(text(agentName));
            return this;
        });
    }

    public AgentsPage agentShouldHaveConnectionStatus(String status) {
        return StepLogger.log("Проверка статуса Connected у агента: " + status, () -> {
            agentConnectionStatus.shouldBe(visible).shouldHave(text(status));
            return this;
        });
    }

    public AgentsPage agentShouldHaveAuthorizationStatus(String status) {
        return StepLogger.log("Проверка статуса авторизации у агента: " + status, () -> {
            agentAuthorizationStatus.shouldBe(visible).shouldHave(text(status));
            return this;
        });
    }

    public AgentsPage agentShouldHaveCommentToAuthorizationStatus(String comment) {
        return StepLogger.log("Проверка комментария к статусу авторизации: " + comment, () -> {
            agentAuthorizationStatusComment.shouldBe(visible).shouldHave(text(comment));
            return this;
        });
    }

    public AgentsPage agentShouldHaveEnabledStatus(String status) {
        return StepLogger.log("Проверка статуса активности агента: " + status, () -> {
            agentEnabledStatus.shouldBe(visible).shouldHave(text(status));
            return this;
        });
    }

    public AgentsPage agentShouldHaveCommentToEnabledStatus(String comment) {
        return StepLogger.log("Проверка комментария к статусу активности: " + comment, () -> {
            agentEnabledStatusComment.shouldBe(visible).shouldHave(text(comment));
            return this;
        });
    }

    public AgentsPage userShouldNotSeeSetAgentStatusButtons() {
        return StepLogger.log("Проверка отсутствия кнопок изменения статуса агента", () -> {
            setEnabledStatusButton.shouldNotBe(visible);
            setAuthorizationStatusButton.shouldNotBe(visible);
            return this;
        });
    }

    public EnableModalWindow clickSetEnabledStatusButton() {
        return StepLogger.log("Клик по кнопке включения агента", () -> {
            EnableModalWindow enableModalWindow = new EnableModalWindow();
            setEnabledStatusButton.shouldBe(visible).shouldHave(text(AgentStatus.ENABLE.getMessage()));
            setEnabledStatusButton.click();
            return enableModalWindow;
        });
    }

    public DisableModalWindow clickSetDisabledStatusButton() {
        return StepLogger.log("Клик по кнопке выключения агента", () -> {
            DisableModalWindow disableModalWindow = new DisableModalWindow();
            setEnabledStatusButton.shouldBe(visible).shouldHave(text(AgentStatus.DISABLE.getMessage()));
            setEnabledStatusButton.click();
            return disableModalWindow;
        });
    }

    public AuthorizeModalWindow clickSetAuthorizationStatusButton() {
        return StepLogger.log("Клик по кнопке авторизации агента", () -> {
            AuthorizeModalWindow authorizeModalWindow = new AuthorizeModalWindow();
            setAuthorizationStatusButton.shouldBe(visible).shouldHave(text(AgentStatus.AUTHORIZE.getMessage()));
            setAuthorizationStatusButton.click();
            return authorizeModalWindow;
        });
    }

    public UnAuthorizeModalWindow clickSetAnAuthorizationStatusButton() {
        return StepLogger.log("Клик по кнопке деавторизации агента", () -> {
            UnAuthorizeModalWindow anAuthorizeModalWindow = new UnAuthorizeModalWindow();
            setAuthorizationStatusButton.shouldBe(visible).shouldHave(text(AgentStatus.UNAUTHORIZE.getMessage()));
            setAuthorizationStatusButton.click();
            return anAuthorizeModalWindow;
        });
    }

    public AgentsPage checkAllAgentStatuses(String connectionStatus, String authorizationStatus, String enabledStatus) {
        return StepLogger.log("Проверка всех статусов агента: соединение=" + connectionStatus +
                ", авторизация=" + authorizationStatus + ", включение=" + enabledStatus, () -> {
            agentShouldHaveConnectionStatus(connectionStatus);
            agentShouldHaveAuthorizationStatus(authorizationStatus);
            agentShouldHaveEnabledStatus(enabledStatus);
            return this;
        });
    }
}
