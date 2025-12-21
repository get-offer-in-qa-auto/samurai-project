package common.storage;

import api.models.users.AuthUser;
import api.models.users.CreateUserRequest;
import api.models.users.CreateUserRoleRequest;
import api.models.users.Roles;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;

public class UserSession {
    private final int index;
    private final CreateUserRequest userRequest;
    private final AuthUser authUser;
    private final String username;
    private final String password;


    private UserSession(int index, CreateUserRequest userRequest, AuthUser authUser) {
        this.index = index;
        this.userRequest = userRequest;
        this.authUser = authUser;
        this.username = null;
        this.password = null;
    }


    private UserSession(int index, String username, String password) {
        this.index = index;
        this.userRequest = null;
        this.authUser = null;
        this.username = username;
        this.password = password;
    }


    public static UserSession create(int index, Roles role) {
        CreateUserRequest userRequest = AdminSteps.createTemporaryUser();
        CreateUserRoleRequest roleRequest = AdminSteps.addRoleForUser(userRequest, role);

        var tokenResponse = UserSteps.createTokenForUser(userRequest);
        String sessionId = UserSteps.getUserSessionIdByToken(
                userRequest.getUsername(),
                userRequest.getPassword()
        );

        AuthUser authUser = new AuthUser(
                userRequest.getUsername(),
                userRequest.getPassword(),
                tokenResponse.getValue(),
                userRequest.getId(),
                roleRequest.getRoleId(),
                sessionId
        );

        return new UserSession(index, userRequest, authUser);
    }


    public static UserSession create(int index, String username, String password, Roles role) {
        CreateUserRequest userRequest = AdminSteps.createTemporaryUser(username, password);
        CreateUserRoleRequest roleRequest = AdminSteps.addRoleForUser(userRequest, role);

        var tokenResponse = UserSteps.createTokenForUser(userRequest);
        String sessionId = UserSteps.getUserSessionIdByToken(
                userRequest.getUsername(),
                userRequest.getPassword()
        );

        AuthUser authUser = new AuthUser(
                userRequest.getUsername(),
                userRequest.getPassword(),
                tokenResponse.getValue(),
                userRequest.getId(),
                roleRequest.getRoleId(),
                sessionId
        );

        return new UserSession(index, userRequest, authUser);
    }


    public static UserSession registerUiUser(int index, String username, String password) {
        return new UserSession(index, username, password);
    }

    public int getIndex() {
        return index;
    }

    public AuthUser getAuthUser() {
        return authUser;
    }

    public String getUsername() {
        return authUser != null ? authUser.getUsername() : username;
    }

    public String getPassword() {
        return authUser != null ? authUser.getPassword() : password;
    }

    public String getSessionId() {
        return authUser != null ? authUser.getSessionId() : null;
    }


    public void delete() {
        if (authUser != null) {
            AdminSteps.deleteUser(userRequest);
        } else {
            AdminSteps.deleteUser(username);
        }
    }
}