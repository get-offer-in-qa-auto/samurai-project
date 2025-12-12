package api.agents;

import api.BaseTest;
import api.generators.RandomData;
import api.models.agent.AgentStatusUpdateRequest;
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
import common.annotations.Agent;
import common.annotations.WithAuthUser;
import common.errors.AgentErrorMessage;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static api.models.agent.GetAgentsRequest.AGENT_AUTHORIZATION;
import static api.models.agent.GetAgentsRequest.AGENT_ENABLING;
import static api.requests.steps.AdminSteps.addRoleForUser;
import static api.requests.steps.AdminSteps.createTemporaryUser;
import static api.requests.steps.AdminSteps.deleteUser;
import static api.requests.steps.UserSteps.getAgentList;
import static api.requests.steps.UserSteps.getAuthorizedAgentInfo;
import static common.extensions.AgentExtension.getCurrentAgent;
import static org.assertj.core.api.Assertions.assertThat;

public class AgentNegativeTest extends BaseTest {

    private static List<CreateUserRequest> users = new CopyOnWriteArrayList<>();

    @Test
    @WithAuthUser(role = Roles.USER_ROLE)
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

    @Agent
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @Test
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

    @Agent
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    @Test
    public void nonAgentManagerCanNotEnableNonExistentAgentTest() {
        api.models.agent.Agent agent = getCurrentAgent();
        int agentId = agent.getId();
        CreateUserRequest user = createTemporaryUser();
        users.add(user);
        addRoleForUser(user, Roles.USER_ROLE);

        AgentStatusUpdateRequest request =
                AgentStatusUpdateRequest.builder()
                        .status(true)
                        .comment(Comment.builder().text(AGENT_ENABLING+ agentId).build())
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
