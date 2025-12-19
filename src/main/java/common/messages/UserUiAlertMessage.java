package common.messages;

import lombok.Getter;

@Getter
public enum UserUiAlertMessage implements HasMessage {
    USERNAME_EMPTY("Username is empty"),
    PASSWORD_EMPTY("Password is empty"),
    PASSWORD_MISMATCH("Passwords mismatch"),
    USERNAME_DUPLICATE("is already in use by some other user.");


    private final String message;

    UserUiAlertMessage(String message) {
        this.message = message;
    }
}