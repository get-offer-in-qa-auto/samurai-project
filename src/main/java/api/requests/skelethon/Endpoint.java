package api.requests.skelethon;

import api.models.BaseModel;
import api.models.agent.GetAgentRequest;
import api.models.agent.GetAgentResponse;
import api.models.agent.GetAuthorizedInfoAgentResponse;
import api.requests.skelethon.requesters.IdentityFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    AGENTS(
            "/app/rest/agents",
            GetAgentRequest.class,
            GetAgentResponse.class
    ),
    AUTHORIZED_INFO_AGENT(
            "/app/rest/agents/{id}/authorizedInfo",
            BaseModel.class,
            GetAuthorizedInfoAgentResponse.class,
            IdentityFormat.TEAMCITY_ID
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
