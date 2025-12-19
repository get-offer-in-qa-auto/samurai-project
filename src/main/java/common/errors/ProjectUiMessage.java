package common.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProjectUiMessage implements HasMessage {
    PROJECT_CREATED("been successfully created"),
    CHANGES_SAVED("Your changes have been saved."),
    ID_MUST_NOT_BE_EMPTY("Project ID must not be empty.");

    private final String message;

}
