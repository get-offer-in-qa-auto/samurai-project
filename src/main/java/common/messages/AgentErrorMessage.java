package common.messages;

import lombok.Getter;

@Getter
public enum AgentErrorMessage implements HasMessage {

    USER_NOT_FOUND("No agent can be found by id '%s'."),
    PERMISSION_DENIED_FOR_AGENT_AUTHORIZATION("You do not have \"Authorize project agent\" permission for pool 'Default'"),
    PERMISSION_DENIED_FOR_AGENT_ENABLE_DISABLE("You do not have \"Enable / disable agents associated with project\" permission for pool 'Default'");


    private final String message;


    AgentErrorMessage(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}