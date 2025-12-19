package common.messages;

import lombok.Getter;

@Getter
public enum ProjectErrorMessage implements HasMessage {
    PROJECT_NAME_EMPTY("Project name cannot be empty"),
    PROJECT_NAME_EXISTS("Project with this name already exists"),
    PROJECT_ID_EMPTY("Project ID must not be empty"),
    PROJECT_NOT_FOUND("No project found by name"),
    PROJECT_ID_TOO_LONG("it is 226 characters long while the maximum length is 225"),
    PROJECT_ID_STARTS_WITH_UNDERSCORE("starts with non-letter character '_'"),
    PROJECT_ID_UNSUPPORTED_CHARACTER("contains unsupported character '-'"),
    PROJECT_ID_NON_LATIN("contains non-latin letter 'д'"),
    PROJECT_ID_STARTS_WITH_DIGIT("starts with non-letter character '1'"),
    PROJECT_ID_ALREADY_USED("is already used by another project");

    private final String message;

    ProjectErrorMessage(String message) {
        this.message = message;
    }
}