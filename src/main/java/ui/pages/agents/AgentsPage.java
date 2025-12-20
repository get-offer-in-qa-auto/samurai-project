package ui.pages.agents;

import com.codeborne.selenide.SelenideElement;
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
        agentsPageTitle.shouldBe(visible).shouldHave(text("Overview"));
        return this;
    }

    public AgentsPage agentPageTitleShouldHaveName(String agentName) {
        agentPageTitle.shouldBe(visible).shouldHave(text(agentName));
        return this;
    }

    public AgentsPage agentShouldHaveConnectionStatus(String status) {
        agentConnectionStatus.shouldBe(visible).shouldHave(text(status));
        return this;
    }

    public AgentsPage agentShouldHaveAuthorizationStatus(String status) {
        agentAuthorizationStatus.shouldBe(visible).shouldHave(text(status));
        return this;
    }

    public AgentsPage agentShouldHaveCommentToAuthorizationStatus(String comment) {
        agentAuthorizationStatusComment.shouldBe(visible).shouldHave(text(comment));
        return this;
    }

    public AgentsPage agentShouldHaveEnabledStatus(String status) {
        agentEnabledStatus.shouldBe(visible).shouldHave(text(status));
        return this;
    }

    public AgentsPage agentShouldHaveCommentToEnabledStatus(String comment) {
        agentEnabledStatusComment.shouldBe(visible).shouldHave(text(comment));
        return this;
    }

    public AgentsPage userShouldNotSeeSetAgentStatusButtons() {
        setEnabledStatusButton.shouldNotBe(visible);
        setAuthorizationStatusButton.shouldNotBe(visible);
        return this;
    }

    public EnableModalWindow clickSetEnabledStatusButton() {
        EnableModalWindow enableModalWindow = new EnableModalWindow();
        setEnabledStatusButton.shouldBe(visible).shouldHave(text(AgentStatus.ENABLE.getMessage()));
        setEnabledStatusButton.click();
        return enableModalWindow;
    }

    public DisableModalWindow clickSetDisabledStatusButton() {
        DisableModalWindow disableModalWindow = new DisableModalWindow();
        setEnabledStatusButton.shouldBe(visible).shouldHave(text(AgentStatus.DISABLE.getMessage()));
        setEnabledStatusButton.click();
        return disableModalWindow;
    }

    public AuthorizeModalWindow clickSetAuthorizationStatusButton() {
        AuthorizeModalWindow authorizeModalWindow = new AuthorizeModalWindow();
        setAuthorizationStatusButton.shouldBe(visible).shouldHave(text(AgentStatus.AUTHORIZE.getMessage()));
        setAuthorizationStatusButton.click();
        return authorizeModalWindow;
    }

    public UnAuthorizeModalWindow clickSetAnAuthorizationStatusButton() {
        UnAuthorizeModalWindow anAuthorizeModalWindow = new UnAuthorizeModalWindow();
        setAuthorizationStatusButton.shouldBe(visible).shouldHave(text(AgentStatus.UNAUTHORIZE.getMessage()));
        setAuthorizationStatusButton.click();
        return anAuthorizeModalWindow;
    }

    public AgentsPage checkAllAgentStatuses(String connectionStatus, String authorizationStatus, String enabledStatus) {
        agentShouldHaveConnectionStatus(connectionStatus);
        agentShouldHaveAuthorizationStatus(authorizationStatus);
        agentShouldHaveEnabledStatus(enabledStatus);
        return this;
    }
}
