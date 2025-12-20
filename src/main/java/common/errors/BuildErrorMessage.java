package common.errors;

import lombok.Getter;

@Getter
public enum BuildErrorMessage implements HasMessage {

    NOTHING_FOUND_BY_LOCATOR("Nothing is found by locator"),
    NO_BUILD_FOUND_BY_ID("No build found by id"),
    NO_BUILD_TYPE_NOR_TEMPLATE_IS_FOUND_BY_ID("No build type nor template is found by id");


    private final String message;

    BuildErrorMessage(String message) {
        this.message = message;
    }

}
