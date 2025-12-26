package api.agents;

import api.BaseTest;
import api.comparison.ModelAssertions;
import api.generators.RandomData;
import api.models.agent.Agent;
import api.models.agent.AgentStatusUpdateRequest;
import api.models.agent.AgentStatusUpdateResponse;
import api.models.agent.Comment;
import api.models.agent.GetAgentsResponse;
import api.models.error.ErrorResponse;
import api.models.users.CreateUserRequest;
import api.models.users.Roles;
import api.models.users.User;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.TestAgent;
import common.annotations.WithAuthUser;
import common.messages.AgentErrorMessage;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static api.models.agent.GetAgentsRequest.AGENT_AUTHORIZATION;
import static api.models.agent.GetAgentsRequest.AGENT_DEAUTHORIZATION;
import static api.models.agent.GetAgentsRequest.AGENT_DISABLING;
import static api.models.agent.GetAgentsRequest.AGENT_ENABLING;
import static api.requests.steps.AdminSteps.addRoleForUser;
import static api.requests.steps.AdminSteps.createTemporaryUser;
import static api.requests.steps.AdminSteps.deleteUser;
import static api.requests.steps.UserSteps.getAgentEnabledInfo;
import static api.requests.steps.UserSteps.getAgentList;
import static api.requests.steps.UserSteps.getAuthorizedAgentInfo;
import static api.requests.steps.UserSteps.setAuthorizationStatusToAgent;
import static api.requests.steps.UserSteps.setEnabledStatusToAgent;
import static common.extensions.AgentExtension.getCurrentAgent;
import static common.extensions.AuthUserExtension.getAuthUser;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateAgentTest extends BaseTest {
    private static List<CreateUserRequest> users = new CopyOnWriteArrayList<>();

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @TestAgent
    @DisplayName("Успешная авторизация агента")
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
    @TestAgent(authorized = true)
    @DisplayName("Успешная деавторизация агента")
    public void userCanDeAuthorizeAgentTest() {
        // 1. Получаем id агента
        Agent agent = getCurrentAgent();
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
    @TestAgent
    @DisplayName("Успешное подключение агента")
    public void userCanEnableAgentTest() {
        // 1. Получаем ID агента
        Agent agent = getCurrentAgent();
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
    @TestAgent(enabled = true)
    @DisplayName("Успешное выключение агента")
    public void userCanDisableAgentTest() {
        // 1. Получаем ID  агента
        Agent agent = getCurrentAgent();
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
    @TestAgent
    @DisplayName("Успешное повторное подключение агента")
    public void reEnablingAgentReturnsSameResultTest() {
        Agent agent = getCurrentAgent();
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
    @TestAgent
    @DisplayName("Успешная повторная авторизация агента")
    public void reAuthorizationAgentReturnsSameResultTest() {
        Agent agent = getCurrentAgent();
        int agentId = agent.getId();
        AgentStatusUpdateRequest enableRequest = AgentStatusUpdateRequest.builder()
                .status(true)
                .comment(Comment.builder().text(AGENT_AUTHORIZATION + agentId).build())
                .build();
        AgentStatusUpdateResponse result1 = setEnabledStatusToAgent(enableRequest, agentId);
        AgentStatusUpdateResponse result2 = setEnabledStatusToAgent(enableRequest, agentId);

        softly.assertThat(result1)
                .usingRecursiveComparison()
                .ignoringFields("comment.timestamp")
                .isEqualTo(result2);
        softly.assertThat(result1.getStatus()).isTrue();
    }

    //Негативные проверки

    @Test
    @WithAuthUser(role = Roles.USER_ROLE)
    @DisplayName("Неуспешная авторизация агента по несуществующему id")
    public void agentManagerCanNotAuthorizedExistentAgentTest() {
        int fakeAgentId = RandomData.getId();

        AgentStatusUpdateRequest request =
                AgentStatusUpdateRequest.builder()
                        .status(true)
                        .comment(Comment.builder().text(AGENT_AUTHORIZATION + fakeAgentId).build())
                        .build();

        ValidatableResponse response = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.AUTHORIZED_INFO_AGENT,
                ResponseSpecs.requestReturns404NotFound())
                .put(request, fakeAgentId);

        ErrorResponse errorResponse = extractError(response);
        assertErrorMessage(errorResponse, AgentErrorMessage.USER_NOT_FOUND.format(fakeAgentId));

        GetAgentsResponse agentList = getAgentList();
        assertThat(agentList.getAgent())
                .extracting(api.models.agent.Agent::getId)
                .doesNotContain(fakeAgentId);
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @DisplayName("Неуспешное подключение агента по несуществующему id")
    public void agentManagerCannotEnableNonExistentAgent() {
        int fakeAgentId = RandomData.getId();

        AgentStatusUpdateRequest enableRequest = AgentStatusUpdateRequest.builder()
                .status(true)
                .comment(Comment.builder().text(AGENT_ENABLING + fakeAgentId).build())
                .build();

        ValidatableResponse response = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.ENABLED_INFO_AGENT,
                ResponseSpecs.requestReturns404NotFound())
                .put(enableRequest, fakeAgentId);

        ErrorResponse errorResponse = extractError(response);
        assertErrorMessage(errorResponse, AgentErrorMessage.USER_NOT_FOUND.format(fakeAgentId));

        GetAgentsResponse agentList = getAgentList();
        assertThat(agentList.getAgent())
                .extracting(api.models.agent.Agent::getId)
                .doesNotContain(fakeAgentId);
    }

    @Test
    @TestAgent
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @DisplayName("Неуспешная авторизация агента пользователем с ролью 'USER'")
    public void nonAgentManagerCanNotAuthorizedAgentTest() {
        api.models.agent.Agent agent = getCurrentAgent();
        int agentId = agent.getId();
        CreateUserRequest user = createTemporaryUser();
        users.add(user);
        addRoleForUser(user, Roles.USER_ROLE);

        AgentStatusUpdateRequest request =
                AgentStatusUpdateRequest.builder()
                        .status(true)
                        .comment(Comment.builder().text(AGENT_AUTHORIZATION + agentId).build())
                        .build();

        ValidatableResponse response = new CrudRequester(
                RequestSpecs.userAuthSpecWithoutToken(user.getUsername(), user.getPassword()),
                Endpoint.AUTHORIZED_INFO_AGENT,
                ResponseSpecs.requestReturns403Forbidden())
                .put(request, agentId);

        ErrorResponse errorResponse = extractError(response);
        assertErrorMessage(errorResponse, AgentErrorMessage.PERMISSION_DENIED_FOR_AGENT_AUTHORIZATION);


        Boolean agentStatus = getAuthorizedAgentInfo(agent.getId()).getStatus();
        softly.assertThat(agentStatus).as("Authorization status must be false").isFalse();
    }

    @Test
    @TestAgent
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @DisplayName("Неуспешное подключение агента пользователем с ролью 'USER'")
    public void nonAgentManagerCanNotEnableNonExistentAgentTest() {
        api.models.agent.Agent agent = getCurrentAgent();
        int agentId = agent.getId();
        CreateUserRequest user = createTemporaryUser();
        users.add(user);
        addRoleForUser(user, Roles.USER_ROLE);

        AgentStatusUpdateRequest request =
                AgentStatusUpdateRequest.builder()
                        .status(true)
                        .comment(Comment.builder().text(AGENT_ENABLING + agentId).build())
                        .build();

        ValidatableResponse response = new CrudRequester(
                RequestSpecs.userAuthSpecWithoutToken(user.getUsername(), user.getPassword()),
                Endpoint.ENABLED_INFO_AGENT,
                ResponseSpecs.requestReturns403Forbidden())
                .put(request, agentId);

        ErrorResponse errorResponse = extractError(response);
        assertErrorMessage(errorResponse, AgentErrorMessage.PERMISSION_DENIED_FOR_AGENT_ENABLE_DISABLE);


        Boolean agentStatus = getAuthorizedAgentInfo(agent.getId()).getStatus();
        softly.assertThat(agentStatus).as("Enable status must be false").isFalse();
    }

    @AfterAll
    public static void cleanTestData() {
        for (CreateUserRequest user : users) {
            try {
                deleteUser(user);
            } catch (Exception e) {
                System.err.println("Failed to delete user: " + user.getUsername() + " – " + e.getMessage());
            }
        }
    }
}
