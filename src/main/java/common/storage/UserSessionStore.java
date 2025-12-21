package common.storage;

import api.models.users.Roles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserSessionStore {
    private static final ThreadLocal<List<UserSession>> USERS =
            ThreadLocal.withInitial(ArrayList::new);

    public static UserSession create() {
        return create(Roles.USER_ROLE);
    }

    public static UserSession create(Roles role) {
        int index = USERS.get().size() + 1;
        UserSession session = UserSession.create(index, role);
        USERS.get().add(session);
        return session;
    }

    public static UserSession create(String username, String password, Roles role) {
        int index = USERS.get().size() + 1;
        UserSession session = UserSession.create(index, username, password, role);
        USERS.get().add(session);
        return session;
    }

    public static UserSession registerUiUser(String username, String password) {
        int index = USERS.get().size() + 1;
        UserSession session = UserSession.registerUiUser(index, username, password);
        USERS.get().add(session);
        return session;
    }

    public static UserSession user(int index) {
        if (index <= 0 || index > USERS.get().size()) {
            throw new IndexOutOfBoundsException(
                    "User " + index + " not found. Created users: " + USERS.get().size()
            );
        }
        return USERS.get().get(index - 1);
    }

    public static List<UserSession> users() {
        return Collections.unmodifiableList(USERS.get());
    }

    public static int count() {
        return USERS.get().size();
    }


    public static void cleanup() {
        USERS.get().forEach(UserSession::delete);
        USERS.remove();
    }
}