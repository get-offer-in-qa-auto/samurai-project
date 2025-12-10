package common.errors;

import lombok.Getter;

@Getter
public enum UserErrorMessage implements HasMessage {
    USERNAME_EMPTY("Username must not be empty when creating user."),
    USERNAME_DUPLICATE("Cannot create user as user with the same username already exists"),
    PASSWORD_EMPTY("Password must not be empty when creating user."),
    USER_NOT_FOUND("User not found"),
    USERNAME_EMPTY_UPDATE("Username cannot be empty"),
    PASSWORD_EMPTY_UPDATE("Password cannot be empty"),
    USERNAME_DUPLICATE_UPDATE("is already in use by some other user.");

    private final String message;

    UserErrorMessage(String message) {
        this.message = message;
    }
}