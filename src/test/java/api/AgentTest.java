package api;

import api.comparison.ModelAssertions;
import api.models.agent.Agent;
import api.models.agent.AgentStatusUpdateRequest;
import api.models.agent.AgentStatusUpdateResponse;
import api.models.agent.Comment;
import api.models.agent.GetAgentsResponse;
import api.models.users.User;
import api.models.users.Roles;
import common.annotations.WithAuthUser;
import org.junit.jupiter.api.Test;

import static api.models.agent.GetAgentsRequest.AGENT_AUTHORIZATION;
import static api.models.agent.GetAgentsRequest.AGENT_DEAUTHORIZATION;
import static api.models.agent.GetAgentsRequest.AGENT_DISABLING;
import static api.models.agent.GetAgentsRequest.AGENT_ENABLING;
import static api.requests.steps.UserSteps.getAgentEnabledInfo;
import static api.requests.steps.UserSteps.getAgentList;
import static api.requests.steps.UserSteps.getAnyAgent;
import static api.requests.steps.UserSteps.setEnabledStatusToAgent;
import static api.requests.steps.UserSteps.setAuthorizationStatusToAgent;
import static api.requests.steps.UserSteps.setDefaultAgentEnabledStatus;
import static api.requests.steps.UserSteps.getAuthorizedAgentInfo;
import static common.extensions.AuthUserExtension.getAuthUser;
import static org.assertj.core.api.Assertions.assertThat;

public class AgentTest extends BaseTest {
    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanAuthorizedAgentTest() {
        // 1. Получаем неавторизованного агента
        Agent agent = getAnyAgent();
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
        assertThat(agent).isEqualTo(getAgentListResponse.getAgent().get(0));

        // 5. Проверяем, что получаем корректную информацию при запросе информации об авторизованном агенте по локатору (id)
        assertThat(authorizationResponse).isEqualTo(getAuthorizedAgentInfo(agent.getId()));
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanDeAuthorizeAgentTest() {
        // 1. Получаем id агента (авторизованного или нет)
        Agent agent = getAnyAgent();
        int agentId = agent.getId();

        // 2. Получаем текущий статус авторизации
        AgentStatusUpdateResponse currentAuthInfo = getAuthorizedAgentInfo(agentId);

        // 3. Если агент НЕ авторизован — сначала авторизуем его
        User user = User.builder().username(getAuthUser().getUsername()).id(getAuthUser().getId()).build();
        if (!currentAuthInfo.getStatus()) {
            AgentStatusUpdateRequest authRequest = AgentStatusUpdateRequest.builder()
                    .status(true)
                    .comment(Comment.builder().text(AGENT_AUTHORIZATION + agentId).user(user).build())
                    .build();
            setAuthorizationStatusToAgent(authRequest, agentId);

        // 4. Проверяем, что агент появился в списке авторизованных
            assertThat(getAgentList().getAgent().get(0).getId()).isEqualTo(agentId);
        }

        // 5. Деавторизуем агент
        AgentStatusUpdateRequest deAuthRequest = AgentStatusUpdateRequest.builder()
                .status(false)
                .comment(Comment.builder().text(AGENT_DEAUTHORIZATION + agentId).user(user).build())
                .build();

        AgentStatusUpdateResponse deAuthResponse = setAuthorizationStatusToAgent(deAuthRequest, agentId);
        ModelAssertions.assertThatModels(deAuthRequest, deAuthResponse).match();

        // 6. Агент исчез из списка авторизованных агентов
        GetAgentsResponse afterDeAuth = getAgentList();
        assertThat(afterDeAuth.getAgent())
                .extracting(Agent::getId)
                .doesNotContain(agentId);
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanEnableAgentTest() {
        // 1. Получаем ID  агента
        Agent agent = getAnyAgent();
        int agentId = agent.getId();

        // 2. Гарантируем, что агент изначально ОТКЛЮЧЕН
        setDefaultAgentEnabledStatus(false);

        // 3. Включаем агента
        User user = User.builder().username(getAuthUser().getUsername()).id(getAuthUser().getId()).build();
        AgentStatusUpdateRequest enableRequest = AgentStatusUpdateRequest.builder()
                .status(true)
                .comment(Comment.builder().text(AGENT_ENABLING + agentId).user(user).build())
                .build();

        AgentStatusUpdateResponse enableResponse = setEnabledStatusToAgent(enableRequest, agentId);

        // 4. Проверяем ответ
        ModelAssertions.assertThatModels(enableRequest, enableResponse).match();

        // 5. Убеждаемся, что статус  изменился
        AgentStatusUpdateResponse currentStatusResponse = getAgentEnabledInfo(agentId);
        assertThat(enableResponse).isEqualTo(currentStatusResponse);
        assertThat(currentStatusResponse.getStatus()).isTrue();
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanDisableAgentTest() {
        // 1. Получаем ID любого агента
        Agent agent = getAnyAgent();
        int agentId = agent.getId();

        // 2. Гарантируем, что агент изначально ВКЛЮЧЕН
        AgentStatusUpdateRequest enableRequest = AgentStatusUpdateRequest.builder()
                .status(true)
                .comment(Comment.builder().text(AGENT_ENABLING + agentId).build())
                .build();
        setEnabledStatusToAgent(enableRequest, agentId);

        // 3. Отключаем агента
        User user = User.builder().username(getAuthUser().getUsername()).id(getAuthUser().getId()).build();
        AgentStatusUpdateRequest disableRequest = AgentStatusUpdateRequest.builder()
                .status(false)
                .comment(Comment.builder().text(AGENT_DISABLING + agentId).user(user).build())
                .build();

        AgentStatusUpdateResponse enableResponse = setEnabledStatusToAgent(disableRequest, agentId);

        // 4. Проверяем ответ
        ModelAssertions.assertThatModels(disableRequest, enableResponse).match();

        // 5. Убеждаемся, что статус изменился
        AgentStatusUpdateResponse currentStatusResponse = getAgentEnabledInfo(agentId);
        assertThat(enableResponse).isEqualTo(currentStatusResponse);
        assertThat(currentStatusResponse.getStatus()).isFalse();
    }
}
