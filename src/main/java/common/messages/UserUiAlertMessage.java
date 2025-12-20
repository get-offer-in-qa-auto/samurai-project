package common.messages;

import lombok.Getter;

@Getter
public enum UserUiAlertMessage implements HasMessage {
    USERNAME_EMPTY("Username is empty"),
    PASSWORD_EMPTY("Password is empty"),
    PASSWORD_MISMATCH("Passwords mismatch"),
    USERNAME_DUPLICATE("is already in use by some other user."),
    SUCCESS_CHANGES("Your changes have been saved."),
    CURRENT_PASSWORD_INCORRECT("Current password is incorrect"),
    DELETE_ALERT("Are you sure you want to delete");

    private final String message;

    UserUiAlertMessage(String message) {
        this.message = message;
    }
}