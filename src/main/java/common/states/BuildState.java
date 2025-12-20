package common.states;

import common.messages.HasMessage;
import lombok.Getter;


@Getter
public enum BuildState implements HasMessage {
    QUEUED("queued"),
    CANCELED("canceled"),
    FINISHED("finished");

    private final String message;

    BuildState(String message) {
        this.message = message;
    }

}
