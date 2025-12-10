package api.requests.steps;

import api.generators.RandomData;
import api.generators.RandomModelGenerator;
import api.models.agent.*;
import api.models.project.CreateProjectFromRepositoryRequest;
import api.models.project.CreateProjectManuallyRequest;
import api.models.project.CreateProjectResponse;
import api.models.project.GetProjectsResponse;
import api.models.project.GetProjectsResponse.Project;
import api.models.users.CreateUserRequest;
import api.models.users.CreateUserTokenRequest;
import api.models.users.CreateUserTokenResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static api.models.agent.GetAgentsRequest.*;
import static org.assertj.core.api.Assertions.assertThat;

public class UserSteps {
    public static CreateUserTokenResponse createTokenForUser(CreateUserRequest request) {
        String tokenName = RandomData.getTokenName();
        CreateUserTokenRequest userRequest = CreateUserTokenRequest.builder()
                .name(tokenName)
                .build();
        return new CrudRequester(
                RequestSpecs.userAuthSpecWithoutToken(request.getUsername(), request.getPassword()),
                Endpoint.USERS_CREATE_TOKEN,
                ResponseSpecs.ignoreErrors()
        ).post(userRequest, request.getId())
                .extract()
                .as(CreateUserTokenResponse.class);
    }

    public static CreateProjectResponse createProjectManually(RequestSpecification requestSpec) {
        return new ValidatedCrudRequester<CreateProjectResponse>(
                requestSpec,
                Endpoint.PROJECT_CREATE_MANUALLY,
                ResponseSpecs.requestReturnsOK())
                .post(RandomModelGenerator.generate(CreateProjectManuallyRequest.class));
    }

    public static CreateProjectResponse createProjectFromRepository(RequestSpecification requestSpec) {
        return new ValidatedCrudRequester<CreateProjectResponse>(
                requestSpec,
                Endpoint.PROJECT_CREATE_FROM_REPOSITORY,
                ResponseSpecs.requestReturnsOK())
                .post(RandomModelGenerator.generate(CreateProjectFromRepositoryRequest.class));
    }


    public static Agent getAnyAgent() {
        GetAgentsResponse response = getAgentList(Map.of("locator", "defaultFilter:false"));
        assertThat(response.getAgent()).isNotEmpty();
        return response.getAgent().get(0);
    }

    public static void setDefaultAgentEnabledStatus(boolean status) {
        int agentId = getAnyAgent().getId();
        String action = status ? AGENT_ENABLING : AGENT_DISABLING;
        AgentStatusUpdateRequest request = AgentStatusUpdateRequest.builder()
                .status(status)
                .comment(Comment.builder().text(action + agentId).build())
                .build();
        setEnabledStatusToAgent(request, agentId);
    }

    public static void setDefaultAgentAuthorizedStatus(boolean status) {
        int agentId = getAnyAgent().getId();
        String action = status ? AGENT_AUTHORIZATION : AGENT_DEAUTHORIZATION;
        AgentStatusUpdateRequest request = AgentStatusUpdateRequest.builder()
                .status(status)
                .comment(Comment.builder().text(action + agentId).build())
                .build();
        setAuthorizationStatusToAgent(request, agentId);
    }

    public static AgentStatusUpdateResponse setAuthorizationStatusToAgent(AgentStatusUpdateRequest request, int id) {
        return new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.AUTHORIZED_INFO_AGENT,
                ResponseSpecs.requestReturnsOK()
        )
                .put(request, id)
                .extract()
                .as(AgentStatusUpdateResponse.class);
    }

    public static AgentStatusUpdateResponse getAuthorizedAgentInfo(int id) {
        return new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.AUTHORIZED_INFO_AGENT,
                ResponseSpecs.requestReturnsOK()
        )
                .get(id)
                .extract()
                .as(AgentStatusUpdateResponse.class);
    }

    public static AgentStatusUpdateResponse setEnabledStatusToAgent(AgentStatusUpdateRequest request, int agentId) {
        return new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.ENABLED_INFO_AGENT,
                ResponseSpecs.requestReturnsOK()
        )
                .put(request, agentId)
                .extract()
                .as(AgentStatusUpdateResponse.class);
    }

    public static AgentStatusUpdateResponse getAgentEnabledInfo(int agentId) {
        return new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.ENABLED_INFO_AGENT,
                ResponseSpecs.requestReturnsOK())
                .get(agentId)
                .extract()
                .as(AgentStatusUpdateResponse.class);
    }

    public static GetAgentsResponse getAgentList() {
        return new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOK())
                .get()
                .extract()
                .as(GetAgentsResponse.class);
    }

    public static GetAgentsResponse getAgentList(Map<String, Object> queryParams) {
        return new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOK())
                .get(queryParams)
                .extract()
                .as(GetAgentsResponse.class);
    }

    public static int getProjectsCount(RequestSpecification requestSpec) {
        return new CrudRequester(
                requestSpec,
                Endpoint.GET_ALL_PROJECTS,
                ResponseSpecs.requestReturnsOK())
                .get()
                .extract()
                .as(GetProjectsResponse.class)
                .getCount();
    }

    public static void deleteProject(CreateProjectResponse project, RequestSpecification requestSpec) {
        new CrudRequester(
                requestSpec,
                Endpoint.PROJECT_DELETE,
                ResponseSpecs.ignoreErrors())
                .delete(project.getId());
    }

    public static Project getProjectById(String projectId, RequestSpecification requestSpec) {
        return new CrudRequester(
                requestSpec,
                Endpoint.GET_PROJECT_BY_ID,
                ResponseSpecs.requestReturnsOK())
                .get(projectId)
                .extract()
                .as(GetProjectsResponse.class)
                .getProject()
                .getFirst();
    }
}
