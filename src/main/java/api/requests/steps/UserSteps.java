package api.requests.steps;

import api.generators.RandomData;
import api.generators.RandomModelGenerator;
import api.models.agent.Agent;
import api.models.agent.AgentStatusUpdateRequest;
import api.models.agent.AgentStatusUpdateResponse;
import api.models.agent.Comment;
import api.models.agent.GetAgentsResponse;
import api.models.buildConfiguration.CreateBuildTypeRequest;
import api.models.buildConfiguration.CreateBuildTypeResponse;
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
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.Map;

import static api.generators.RandomData.getBuildName;
import static api.models.agent.GetAgentsRequest.AGENT_AUTHORIZATION;
import static api.models.agent.GetAgentsRequest.AGENT_DEAUTHORIZATION;
import static api.models.agent.GetAgentsRequest.AGENT_DISABLING;
import static api.models.agent.GetAgentsRequest.AGENT_ENABLING;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class UserSteps {
    public static CreateUserTokenResponse createTokenForUser(CreateUserRequest request) {
        String tokenName = RandomData.getTokenName();
        CreateUserTokenRequest userRequest = CreateUserTokenRequest.builder()
                .name(tokenName)
                .build();
        return new CrudRequester(
                RequestSpecs.userAuthSpecWithoutToken(request.getUsername(), request.getPassword()),
                Endpoint.USER_CREATE_TOKEN,
                ResponseSpecs.ignoreErrors()
        ).post(userRequest, request.getId())
                .extract()
                .as(CreateUserTokenResponse.class);
    }

    public static String getUserSessionIdByToken(String username, String password) {

        Response response = given()
                .spec(RequestSpecs.userAuthSpecWithoutToken(username, password))
                .get("/");

        String sessionId = response.getCookie("TCSESSIONID");

        if (sessionId == null) {
            throw new IllegalStateException("TCSESSIONID not found for user");
        }

        return sessionId;
    }

    public static CreateProjectResponse createProjectManually(RequestSpecification requestSpec) {
        return new ValidatedCrudRequester<CreateProjectResponse>(
                requestSpec,
                Endpoint.PROJECT_CREATE_MANUALLY,
                ResponseSpecs.requestReturnsOK())
                .post(CreateProjectManuallyRequest.builder().name(RandomData.getProjectName()).id(RandomData.getProjectId(20)).build());
    }

    public static CreateProjectResponse createProjectFromRepository(RequestSpecification requestSpec) {
        return new ValidatedCrudRequester<CreateProjectResponse>(
                requestSpec,
                Endpoint.PROJECT_CREATE_FROM_REPOSITORY,
                ResponseSpecs.requestReturnsOK())
                .post(RandomModelGenerator.generate(CreateProjectFromRepositoryRequest.class));
    }

    public static List<Agent> getAllAgents() {
        GetAgentsResponse response = getAgentList(Map.of("locator", "defaultFilter:false"));
        assertThat(response.getAgent()).isNotEmpty();
        return response.getAgent();
    }

    public static void setAgentEnabledStatus(boolean status, int agentId) {
        String action = status ? AGENT_ENABLING : AGENT_DISABLING;
        AgentStatusUpdateRequest request = AgentStatusUpdateRequest.builder()
                .status(status)
                .comment(Comment.builder().text(action + agentId).build())
                .build();
        setEnabledStatusToAgent(request, agentId);
    }

    public static void setAgentAuthorizedStatus(boolean status, int agentId) {
        String action = status ? AGENT_AUTHORIZATION : AGENT_DEAUTHORIZATION;
        AgentStatusUpdateRequest request = AgentStatusUpdateRequest.builder()
                .status(status)
                .comment(Comment.builder().text(action + agentId).build())
                .build();
        setAuthorizationStatusToAgent(request, agentId);
    }

    public static AgentStatusUpdateResponse setAuthorizationStatusToAgent(AgentStatusUpdateRequest request, int id) {
        return new ValidatedCrudRequester<AgentStatusUpdateResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.AUTHORIZED_INFO_AGENT,
                ResponseSpecs.requestReturnsOK())
                .put(request, id);
    }

    public static AgentStatusUpdateResponse getAuthorizedAgentInfo(int id) {
        return new ValidatedCrudRequester<AgentStatusUpdateResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.AUTHORIZED_INFO_AGENT,
                ResponseSpecs.requestReturnsOK())
                .get(id);
    }

    public static AgentStatusUpdateResponse setEnabledStatusToAgent(AgentStatusUpdateRequest request, int agentId) {
        return new ValidatedCrudRequester<AgentStatusUpdateResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.ENABLED_INFO_AGENT,
                ResponseSpecs.requestReturnsOK())
                .put(request, agentId);
    }

    public static AgentStatusUpdateResponse getAgentEnabledInfo(int agentId) {
        return new ValidatedCrudRequester<AgentStatusUpdateResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.ENABLED_INFO_AGENT,
                ResponseSpecs.requestReturnsOK())
                .get(agentId);
    }

    public static GetAgentsResponse getAgentList() {
        return new ValidatedCrudRequester<GetAgentsResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOK())
                .get();
    }

    public static GetAgentsResponse getAgentList(Map<String, Object> queryParams) {
        return new ValidatedCrudRequester<GetAgentsResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOK())
                .get(queryParams);
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

    public static List<Project> getProjects(RequestSpecification requestSpec) {
        return new CrudRequester(
                requestSpec,
                Endpoint.GET_ALL_PROJECTS,
                ResponseSpecs.requestReturnsOK())
                .get()
                .extract()
                .as(GetProjectsResponse.class)
                .project;
    }

    public static void deleteProjectById(CreateProjectResponse project, RequestSpecification requestSpec) {
        new CrudRequester(
                requestSpec,
                Endpoint.PROJECT_DELETE,
                ResponseSpecs.ignoreErrors())
                .deleteById(project.getId());
    }

    public static void deleteProjectByName(String name, RequestSpecification requestSpec) {
        new CrudRequester(
                requestSpec,
                Endpoint.PROJECT_DELETE_BY_NAME,
                ResponseSpecs.ignoreErrors())
                .deleteByName(name);
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

    public static String getFirstProjects(RequestSpecification requestSpec) {
        GetProjectsResponse response = new CrudRequester(
                requestSpec,
                Endpoint.GET_ALL_PROJECTS,
                ResponseSpecs.requestReturnsOK())
                .get()
                .extract()
                .as(GetProjectsResponse.class);

        return response.getProject().get(1).getId();
    }

    public static void deleteBuildType(CreateProjectResponse project, RequestSpecification requestSpec) {
        new CrudRequester(
                requestSpec,
                Endpoint.DELETE_BUILD_CONFIGURATION,
                ResponseSpecs.ignoreErrors())
                .deleteById(project.getId());
    }

    public static String getBuildTypeById(String buildId, RequestSpecification requestSpec) {
        CreateBuildTypeResponse response = new CrudRequester(
                requestSpec,
                Endpoint.GET_BUILD_CONFIGURATION,
                ResponseSpecs.requestReturnsOK())
                .get(buildId)
                .extract()
                .as(CreateBuildTypeResponse.class);

        return response.getName();
    }

    public static String createBuildType(String projectId, RequestSpecification requestSpec) {
        String newName = getBuildName();
        api.models.buildConfiguration.Project project = api.models.buildConfiguration.Project.builder()
                .id(projectId)
                .build();
        CreateBuildTypeRequest createBuildTypeRequest = CreateBuildTypeRequest.builder()
                .name(newName)
                .project(project)
                .build();

        CreateBuildTypeResponse response = new CrudRequester(
                requestSpec,
                Endpoint.BUILD_CONFIGURATION_CREATE,
                ResponseSpecs.requestReturnsOK())
                .post(createBuildTypeRequest)
                .extract()
                .as(CreateBuildTypeResponse.class);

        return response.getId();
    }

    public static int getBuildTypeCount(RequestSpecification requestSpec) {
        return new CrudRequester(
                requestSpec,
                Endpoint.GET_ALL_BUILD_CONFIGURATION,
                ResponseSpecs.requestReturnsOK())
                .get()
                .extract()
                .as(GetProjectsResponse.class)
                .getCount();
    }
}
