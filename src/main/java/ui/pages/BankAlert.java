package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlert {
    BUILD_CONFIGURATION_CREATE_SUCCESSFULLY("Build configuration successfully created. You can now configure VCS roots."),
    CHANGES_SAVED("Your changes have been saved.");

    private final String message;

    BankAlert(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
