package api.requests.skelethon;

import api.models.BaseModel;
import api.models.agent.GetAgentRequest;
import api.models.agent.GetAgentResponse;
import api.models.agent.GetAuthorizedInfoAgentResponse;
import api.models.project.CreateProjectFromRepositoryRequest;
import api.models.project.CreateProjectManuallyRequest;
import api.models.project.CreateProjectResponse;
import api.models.users.*;
import api.requests.skelethon.requesters.IdentityFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    AGENTS(
            "/agents",
            GetAgentRequest.class,
            GetAgentResponse.class
    ),
    AUTHORIZED_INFO_AGENT(
            "/agents/{id}/authorizedInfo",
            BaseModel.class,
            GetAuthorizedInfoAgentResponse.class,
            IdentityFormat.TEAMCITY_ID
    ),
    GET_ALL_USERS(
            "/users",
            BaseModel.class,
            GetAllUsersResponse.class
    ),
    USERS_CREATE(
            "/users",
            CreateUserRequest.class,
            CreateUserResponse.class
    ),
    USERS_CREATE_TOKEN(
            "/users/{id}/tokens",
            CreateUserTokenRequest.class,
            CreateUserTokenResponse.class,
            IdentityFormat.TEAMCITY_ID
    ),
    USERS_CREATE_ROLE(
            "/users/{id}/roles",
            CreateUserRoleRequest.class,
            CreateUserRoleResponse.class,
            IdentityFormat.TEAMCITY_ID
    ),
    USERS_DELETE(
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
}
