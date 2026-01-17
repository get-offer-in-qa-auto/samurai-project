package common.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BuildTypeErrorMessage implements HasMessage {
    BUILD_TYPE_NAME_EMPTY("When creating a build type, non empty name should be provided."),
    BUILD_TYPE_NAME_ALREADY_USE("Build configuration with this name already exists"),
    CREATE_DUPLICATE_NAME("Build configuration with name"),
    EDIT_DUPLICATE_NAME("Build configuration with this name already exists");

    private final String message;

}
