package ui.pages.agents;

import lombok.Getter;

@Getter
public enum AgentStatus {
    CONNECTED("Connected"),
    DISABLED("Disabled"),
    ENABLED("Enabled"),
    ENABLE("Enable"),
    DISABLE("Disable"),
    EMPTY_STATUS(""),
    IDLE("idle"),
    UNAUTHORIZED("Unauthorized"),
    AUTHORIZED("Authorized"),
    AUTHORIZE("Authorize"),
    UNAUTHORIZE("Unauthorize");

    private final String message;

    AgentStatus(String message) {
        this.message = message;
    }
}
