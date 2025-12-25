package ui.agents;

import api.models.agent.Agent;
import api.models.agent.AgentStatusUpdateResponse;
import api.models.users.Roles;
import common.annotations.TestAgent;
import common.annotations.WithAuthUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.agents.AgentsPage;
import ui.pages.agents.AgentsSidebar;
import ui.pages.agents.modalWindows.AuthorizeModalWindow;
import ui.pages.agents.modalWindows.DisableModalWindow;
import ui.pages.agents.modalWindows.EnableModalWindow;
import ui.pages.agents.modalWindows.UnAuthorizeModalWindow;

import java.util.List;
import java.util.stream.Collectors;

import static api.models.agent.GetAgentsRequest.AGENT_AUTHORIZATION;
import static api.models.agent.GetAgentsRequest.AGENT_ENABLING;
import static api.requests.steps.UserSteps.*;
import static common.extensions.AgentExtension.getCurrentAgent;
import static ui.pages.agents.AgentStatus.*;

public class AgentsUiTest extends BaseUiTest {
    private AgentsPage agentsPage = new AgentsPage();
    private AgentsSidebar sidebarAgentsPanel = new AgentsSidebar();

    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @TestAgent
    @Test
    @DisplayName("Проверка идентичности списка агентов в UI и API")
    public void shouldDisplaySameAgentsInUiAsInApi() {
        new AgentsPage().open();
        agentsPage.agentsPageShouldBeOpen();
        List<String> allAgentsUi = sidebarAgentsPanel
                .checkAgentsSideBarTitle()
                .expandListOfAgents()
                .getAgentNameAndStatusMap()
                .keySet().stream().toList();

        List<String> allAgentsApi = getAllAgents().stream()
                .map(Agent::getName)
                .collect(Collectors.toList());
        softly.assertThat(allAgentsUi).as("Список агентов UI не совпадет со списком значений API")
                .containsExactlyInAnyOrderElementsOf(allAgentsApi);
    }

    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @TestAgent
    @Test
    @DisplayName("Успешное подключение агента")
    public void userCanEnabledAgent() {
        new AgentsPage().open();
        agentsPage.agentsPageShouldBeOpen();
        Agent agent = getCurrentAgent();
        String agentName = agent.getName();
        sidebarAgentsPanel
                .expandListOfAgents()
                .clickAgentByName(agentName);

        EnableModalWindow enableModalWindow = agentsPage.agentPageTitleShouldHaveName(agentName)
                .checkAllAgentStatuses(CONNECTED.getMessage(), UNAUTHORIZED.getMessage(), DISABLED.getMessage())
                .clickSetEnabledStatusButton();

        enableModalWindow
                .setEnableModalWindowShouldHaveTitle(agentName)
                .setComment(AGENT_ENABLING)
                .clickConfirmButton();

        agentsPage
                .checkAllAgentStatuses(CONNECTED.getMessage(), UNAUTHORIZED.getMessage(), ENABLED.getMessage())
                .agentShouldHaveCommentToEnabledStatus(AGENT_ENABLING);

        sidebarAgentsPanel
                .checkStatusAgentByName(agentName, EMPTY_STATUS.getMessage());

        AgentStatusUpdateResponse enabledStatusResponse = getAgentEnabledInfo(agent.getId());
        softly.assertThat(enabledStatusResponse.getStatus()).isTrue();
    }

    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @TestAgent(enabled = true)
    @Test
    @DisplayName("Успешное отключение агента")
    public void userCanDisabledAgent() {
        new AgentsPage().open();
        agentsPage.agentsPageShouldBeOpen();
        Agent agent = getCurrentAgent();
        String agentName = agent.getName();

        sidebarAgentsPanel
                .expandListOfAgents()
                .clickAgentByName(agentName);

        DisableModalWindow disableModalWindow = agentsPage
                .clickSetDisabledStatusButton();
        disableModalWindow
                .disableModalWindowShouldHaveTitle(agentName)
                .clickConfirmButton();

        sidebarAgentsPanel
                .checkStatusAgentByName(agentName, DISABLED.getMessage());

        agentsPage
                .checkAllAgentStatuses(CONNECTED.getMessage(), UNAUTHORIZED.getMessage(), DISABLED.getMessage());
        AgentStatusUpdateResponse disabledStatusResponse = getAgentEnabledInfo(agent.getId());
        softly.assertThat(disabledStatusResponse.getStatus()).isFalse();
    }

    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @TestAgent
    @Test
    @DisplayName("Успешная авторизация агента")
    public void userCanAuthorizedAgent() {
        new AgentsPage().open();
        agentsPage.agentsPageShouldBeOpen();
        Agent agent = getCurrentAgent();
        String agentName = agent.getName();

        sidebarAgentsPanel
                .expandListOfAgents()
                .clickAgentByName(agentName);

        AuthorizeModalWindow authorizeModalWindow = agentsPage.agentPageTitleShouldHaveName(agentName)
                .checkAllAgentStatuses(CONNECTED.getMessage(), UNAUTHORIZED.getMessage(), DISABLED.getMessage())
                .clickSetAuthorizationStatusButton();

        authorizeModalWindow
                .authorizeModalWindowShouldHaveTitle(agentName)
                .setAuthorizeComment(AGENT_AUTHORIZATION)
                .clickConfirmButton();
        sidebarAgentsPanel
                .findAgentInPoolsByName(agentName)
                .checkStatusAgentByName(agentName, DISABLED.getMessage());

        agentsPage
                .checkAllAgentStatuses(CONNECTED.getMessage(), AUTHORIZED.getMessage(), DISABLED.getMessage())
                .agentShouldHaveCommentToAuthorizationStatus(AGENT_AUTHORIZATION);
        AgentStatusUpdateResponse agentStatusUpdateResponse = getAuthorizedAgentInfo(agent.getId());
        softly.assertThat(agentStatusUpdateResponse.getStatus()).isTrue();
    }

    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @TestAgent(authorized = true)
    @Test
    @DisplayName("Успешная деавторизация агента")
    public void userCanAnAuthorizedAgent() {
        new AgentsPage().open();
        agentsPage.agentsPageShouldBeOpen();
        Agent agent = getCurrentAgent();
        String agentName = agent.getName();

        sidebarAgentsPanel
                .clickAgentInPoolsByName(agentName);

        UnAuthorizeModalWindow anAuthorizeModalWindow = agentsPage.agentPageTitleShouldHaveName(agentName)
                .checkAllAgentStatuses(CONNECTED.getMessage(), AUTHORIZED.getMessage(), DISABLED.getMessage())
                .clickSetAnAuthorizationStatusButton();

        anAuthorizeModalWindow
                .unAuthoriseModalWindowShouldHaveTitle(agentName)
                .clickConfirmButton();

        sidebarAgentsPanel
                .expandListOfAgents()
                .checkStatusAgentByName(agentName, DISABLED.getMessage());

        agentsPage
                .checkAllAgentStatuses(CONNECTED.getMessage(), UNAUTHORIZED.getMessage(), DISABLED.getMessage());
        AgentStatusUpdateResponse agentStatusUpdateResponse = getAuthorizedAgentInfo(agent.getId());
        softly.assertThat(agentStatusUpdateResponse.getStatus()).isFalse();
    }

    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @TestAgent
    @Test
    @DisplayName("Успешные авторизация и подключение агента")
    public void userCanAuthorizedAndEnabledAgent() {
        new AgentsPage().open();
        agentsPage.agentsPageShouldBeOpen();
        Agent agent = getCurrentAgent();
        String agentName = agent.getName();

        sidebarAgentsPanel
                .expandListOfAgents()
                .clickAgentByName(agentName);
        EnableModalWindow enableModalWindow = agentsPage.agentPageTitleShouldHaveName(agentName)
                .checkAllAgentStatuses(CONNECTED.getMessage(), UNAUTHORIZED.getMessage(), DISABLED.getMessage())
                .clickSetEnabledStatusButton();
        enableModalWindow
                .setEnableModalWindowShouldHaveTitle(agentName)
                .clickConfirmButton();

        sidebarAgentsPanel
                .expandListOfAgents()
                .clickAgentByName(agentName);
        AuthorizeModalWindow authorizeModalWindow = agentsPage.agentPageTitleShouldHaveName(agentName)
                .clickSetAuthorizationStatusButton();
        authorizeModalWindow
                .authorizeModalWindowShouldHaveTitle(agentName)
                .clickConfirmButton();

        sidebarAgentsPanel
                .findAgentInPoolsByName(agentName)
                .checkStatusAgentByName(agentName, IDLE.getMessage());

        agentsPage
                .checkAllAgentStatuses(CONNECTED.getMessage(), AUTHORIZED.getMessage(), ENABLED.getMessage());

        AgentStatusUpdateResponse enabledStatusResponse = getAgentEnabledInfo(agent.getId());
        softly.assertThat(enabledStatusResponse.getStatus()).isTrue();

        AgentStatusUpdateResponse agentStatusUpdateResponse = getAuthorizedAgentInfo(agent.getId());
        softly.assertThat(agentStatusUpdateResponse.getStatus()).isTrue();
    }

    @WithAuthUser(role = Roles.USER_ROLE)
    @Test
    @DisplayName("Проверка отсутствия кнопок управления статусом агента у пользователя без прав")
    public void userShouldNotSeeSetAgentStatusButtons() {
        new AgentsPage().open();
        Agent agent = getCurrentAgent();
        agentsPage.agentsPageShouldBeOpen();
        sidebarAgentsPanel
                .expandListOfAgents();
        agentsPage
                .userShouldNotSeeSetAgentStatusButtons();
        setAgentAuthorizedStatus(true, agent.getId());
    }
}
