package api.requests.skelethon;

import api.models.BaseModel;
import api.models.agent.AgentStatusUpdateResponse;
import api.models.agent.GetAgentsResponse;
import api.models.agent.GetAuthorizedInfoAgentResponse;
import api.models.buildConfiguration.CreateBuildTypeRequest;
import api.models.buildConfiguration.CreateBuildTypeResponse;
import api.models.buildConfiguration.GetAllBuildTypeResponse;
import api.models.project.CreateProjectFromRepositoryRequest;
import api.models.project.CreateProjectManuallyRequest;
import api.models.project.CreateProjectResponse;
import api.models.project.GetProjectsResponse;
import api.models.users.*;
import api.requests.skelethon.requesters.IdentityFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    AGENTS(
            "/agents",
            BaseModel.class,
            GetAgentsResponse.class
    ),
    AUTHORIZED_INFO_AGENT(
            "/agents/{id}/authorizedInfo",
            BaseModel.class,
            GetAuthorizedInfoAgentResponse.class,
            IdentityFormat.TEAMCITY_ID
    ),
    ENABLED_INFO_AGENT(
            "/agents/{id}/enabledInfo",
            BaseModel.class,
            AgentStatusUpdateResponse.class,
            IdentityFormat.TEAMCITY_ID
    ),
    GET_ALL_USERS(
            "/users",
            BaseModel.class,
            GetAllUsersResponse.class
    ),
    USER_CREATE(
            "/users",
            CreateUserRequest.class,
            CreateUserResponse.class
    ),
    USER_UPDATE(
            "/users/{id}",
            UpdateUserRequest.class,
            UpdateUserResponse.class,
            IdentityFormat.TEAMCITY_ID
    ),
    USER_CREATE_TOKEN(
            "/users/{id}/tokens",
            CreateUserTokenRequest.class,
            CreateUserTokenResponse.class,
            IdentityFormat.TEAMCITY_ID
    ),
    USER_CREATE_ROLE(
            "/users/{id}/roles",
            CreateUserRoleRequest.class,
            CreateUserRoleResponse.class,
            IdentityFormat.TEAMCITY_ID
    ),
    GET_USER_ROLE(
            "/users/{id}/roles",
            BaseModel.class,
            GetUserRoleResponse.class,
            IdentityFormat.TEAMCITY_ID
    ),
    USER_DELETE(
            "/users/{id}",
            BaseModel.class,
            BaseModel.class,
            IdentityFormat.TEAMCITY_ID
    ),
    PROJECT_CREATE_MANUALLY(
            "/projects",
            CreateProjectManuallyRequest.class,
            CreateProjectResponse.class
    ),
    PROJECT_CREATE_FROM_REPOSITORY(
            "/projects",
            CreateProjectFromRepositoryRequest.class,
            CreateProjectResponse.class
    ),
    BUILD_CONFIGURATION_CREATE(
            "/buildTypes",
            CreateBuildTypeRequest.class,
            CreateBuildTypeResponse.class
    ),
    UPDATE_BUILD_CONFIGURATION_NAME(
            "/buildTypes/{id}/name",
            BaseModel.class,
            BaseModel.class,
            IdentityFormat.TEAMCITY_NAME
    ),
    DELETE_BUILD_CONFIGURATION(
            "/buildTypes/{id}",
            BaseModel.class,
            BaseModel.class,
            IdentityFormat.TEAMCITY_ID
    ),
    GET_ALL_BUILD_CONFIGURATION(
            "/buildTypes",
            BaseModel.class,
            GetAllBuildTypeResponse.class
    ),
    GET_BUILD_CONFIGURATION(
            "/buildTypes/{id}",
            BaseModel.class,
            CreateBuildTypeResponse.class,
            IdentityFormat.TEAMCITY_ID
    ),
    PROJECT_DELETE(
            "/projects/{id}",
            BaseModel.class,
            BaseModel.class,
            IdentityFormat.TEAMCITY_ID
    ),
    GET_ALL_PROJECTS(
            "/projects",
            BaseModel.class,
            GetProjectsResponse.class
    ),
    GET_PROJECT_BY_ID(
            "/projects/?locator={id}",
            BaseModel.class,
            GetProjectsResponse.class,
            IdentityFormat.TEAMCITY_ID
    ),
    PROJECT_UPDATE(
            "/projects/{id}/description",
            BaseModel.class,
            BaseModel.class
    );


    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
    private final IdentityFormat idFormat;

    Endpoint(String url, Class<? extends BaseModel> requestModel, Class<? extends BaseModel> responseModel) {
        this(url, requestModel, responseModel, IdentityFormat.NUMBER);
    }

    public String formatId(int id) {
        if (idFormat == null || idFormat == IdentityFormat.NUMBER) {
            return String.valueOf(id);
        } else if (idFormat == IdentityFormat.TEAMCITY_ID) {
            return "id:" + id;
        }
        throw new IllegalArgumentException("Unsupported IdFormat: " + idFormat);
    }

    public String formatId(String id) {
        return id;
    }
}