package api.agents;

import api.BaseTest;
import api.comparison.ModelAssertions;
import api.models.agent.AgentStatusUpdateRequest;
import api.models.agent.AgentStatusUpdateResponse;
import api.models.agent.Comment;
import api.models.agent.GetAgentsResponse;
import api.models.users.Roles;
import api.models.users.User;
import common.annotations.Agent;
import common.annotations.WithAuthUser;
import org.junit.jupiter.api.Test;

import static api.models.agent.GetAgentsRequest.AGENT_AUTHORIZATION;
import static api.models.agent.GetAgentsRequest.AGENT_DEAUTHORIZATION;
import static api.models.agent.GetAgentsRequest.AGENT_DISABLING;
import static api.models.agent.GetAgentsRequest.AGENT_ENABLING;
import static api.requests.steps.UserSteps.getAgentEnabledInfo;
import static api.requests.steps.UserSteps.getAgentList;
import static api.requests.steps.UserSteps.getAuthorizedAgentInfo;
import static api.requests.steps.UserSteps.setAuthorizationStatusToAgent;
import static api.requests.steps.UserSteps.setEnabledStatusToAgent;
import static common.extensions.AgentExtension.getCurrentAgent;
import static common.extensions.AuthUserExtension.getAuthUser;

public class AgentTest extends BaseTest {
    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @Agent
    public void userCanAuthorizedAgentTest() {
        // 1. Получаем агента
        api.models.agent.Agent agent = getCurrentAgent();
        int agentId = agent.getId();
        User user = User.builder().username(getAuthUser().getUsername()).id(getAuthUser().getId()).build();

        // 2. Формируем запрос на авторизацию
        AgentStatusUpdateRequest request =
                AgentStatusUpdateRequest.builder()
                        .status(true)
                        .comment(Comment.builder().text(AGENT_AUTHORIZATION + agentId).user(user).build())
                        .build();

        // 3. Авторизуем агента
        AgentStatusUpdateResponse authorizationResponse = setAuthorizationStatusToAgent(request, agentId);
        ModelAssertions.assertThatModels(request, authorizationResponse).match();

        // 4. Проверяем, что агент теперь в списке авторизованных агентов
        GetAgentsResponse getAgentListResponse = getAgentList();
        softly.assertThat(agent).isEqualTo(getAgentListResponse.getAgent().get(0));

        // 5. Проверяем, что получаем корректную информацию при запросе информации об авторизованном агенте по локатору (id)
        softly.assertThat(authorizationResponse).isEqualTo(getAuthorizedAgentInfo(agent.getId()));
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @Agent(authorized = true)
    public void userCanDeAuthorizeAgentTest() {
        // 1. Получаем id агента
        api.models.agent.Agent agent = getCurrentAgent();
        int agentId = agent.getId();

        // 2. Деавторизуем агент
        User user = User.builder().username(getAuthUser().getUsername()).id(getAuthUser().getId()).build();

        AgentStatusUpdateRequest deAuthRequest = AgentStatusUpdateRequest.builder()
                .status(false)
                .comment(Comment.builder().text(AGENT_DEAUTHORIZATION + agentId).user(user).build())
                .build();

        // 3. Проверка полей запрос/ответ
        AgentStatusUpdateResponse deAuthResponse = setAuthorizationStatusToAgent(deAuthRequest, agentId);
        ModelAssertions.assertThatModels(deAuthRequest, deAuthResponse).match();

        // 4. Проверка -агент исчез из списка авторизованных агентов
        GetAgentsResponse afterDeAuth = getAgentList();
        softly.assertThat(afterDeAuth.getAgent())
                .extracting(api.models.agent.Agent::getId)
                .doesNotContain(agentId);
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @Agent
    public void userCanEnableAgentTest() {
        // 1. Получаем ID агента
        api.models.agent.Agent agent = getCurrentAgent();
        int agentId = agent.getId();

        // 2. Включаем агента
        User user = User.builder().username(getAuthUser().getUsername()).id(getAuthUser().getId()).build();
        AgentStatusUpdateRequest enableRequest = AgentStatusUpdateRequest.builder()
                .status(true)
                .comment(Comment.builder().text(AGENT_ENABLING + agentId).user(user).build())
                .build();

        AgentStatusUpdateResponse enableResponse = setEnabledStatusToAgent(enableRequest, agentId);

        // 3. Проверяем ответ
        ModelAssertions.assertThatModels(enableRequest, enableResponse).match();

        // 4. Убеждаемся, что статус  изменился
        AgentStatusUpdateResponse currentStatusResponse = getAgentEnabledInfo(agentId);
        softly.assertThat(enableResponse).isEqualTo(currentStatusResponse);
        softly.assertThat(currentStatusResponse.getStatus()).isTrue();
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @Agent(enabled = true)
    public void userCanDisableAgentTest() {
        // 1. Получаем ID  агента
        api.models.agent.Agent agent = getCurrentAgent();
        int agentId = agent.getId();

        // 2. Отключаем агента
        User user = User.builder().username(getAuthUser().getUsername()).id(getAuthUser().getId()).build();
        AgentStatusUpdateRequest disableRequest = AgentStatusUpdateRequest.builder()
                .status(false)
                .comment(Comment.builder().text(AGENT_DISABLING + agentId).user(user).build())
                .build();

        AgentStatusUpdateResponse enableResponse = setEnabledStatusToAgent(disableRequest, agentId);

        // 3. Проверяем ответ
        ModelAssertions.assertThatModels(disableRequest, enableResponse).match();

        // 4. Убеждаемся, что статус изменился
        AgentStatusUpdateResponse currentStatusResponse = getAgentEnabledInfo(agentId);
        softly.assertThat(enableResponse).isEqualTo(currentStatusResponse);
        softly.assertThat(currentStatusResponse.getStatus()).isFalse();
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @Agent
    public void reEnablingAgentReturnsSameResultTest() {
        api.models.agent.Agent agent = getCurrentAgent();
        int agentId = agent.getId();
        AgentStatusUpdateRequest enableRequest = AgentStatusUpdateRequest.builder()
                .status(true)
                .comment(Comment.builder().text(AGENT_ENABLING + agentId).build())
                .build();
        AgentStatusUpdateResponse result1 = setEnabledStatusToAgent(enableRequest, agentId);
        AgentStatusUpdateResponse result2 = setEnabledStatusToAgent(enableRequest, agentId);

        softly.assertThat(result1).isEqualTo(result2);
        softly.assertThat(result1.getStatus()).isTrue();
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @Agent
    public void reAuthorizationAgentReturnsSameResultTest() {
        api.models.agent.Agent agent = getCurrentAgent();
        int agentId = agent.getId();
        AgentStatusUpdateRequest enableRequest = AgentStatusUpdateRequest.builder()
                .status(true)
                .comment(Comment.builder().text(AGENT_AUTHORIZATION + agentId).build())
                .build();
        AgentStatusUpdateResponse result1 = setEnabledStatusToAgent(enableRequest, agentId);
        AgentStatusUpdateResponse result2 = setEnabledStatusToAgent(enableRequest, agentId);

        softly.assertThat(result1).isEqualTo(result2);
        softly.assertThat(result1.getStatus()).isTrue();
    }
}
