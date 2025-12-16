package common.errors;

import lombok.Getter;

@Getter
public enum UserUiAlertMessage implements HasMessage {
    USERNAME_EMPTY("Username is empty"),
    PASSWORD_MISMATCH("Passwords mismatch");


    private final String message;

    UserUiAlertMessage(String message) {
        this.message = message;
    }
}