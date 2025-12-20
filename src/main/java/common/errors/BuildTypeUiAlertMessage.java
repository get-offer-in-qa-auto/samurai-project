package common.errors;

import lombok.Getter;

@Getter
public enum BuildTypeUiAlertMessage implements HasMessage{
    CREATE_DUPLICATE_NAME("Build configuration with name"),
    EDIT_DUPLICATE_NAME("Build configuration with this name already exists");

    private final String message;

    BuildTypeUiAlertMessage(String message) {
        this.message = message;
    }
}
