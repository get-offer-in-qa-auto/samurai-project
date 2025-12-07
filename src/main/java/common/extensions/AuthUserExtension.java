package common.extensions;

import api.models.users.AuthUser;
import api.models.users.CreateUserRequest;
import api.models.users.Roles;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import common.annotations.WithAuthUser;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public class AuthUserExtension implements BeforeEachCallback, AfterEachCallback {

    private static final ThreadLocal<AuthUser> threadLocalAuthUser = new ThreadLocal<>();
    private static final ThreadLocal<CreateUserRequest> threadLocalUserRequest = new ThreadLocal<>();

    @Override
    public void beforeEach(ExtensionContext context) {
        Roles role = context.getTestMethod()
                .flatMap(method -> java.util.Optional.ofNullable(method.getAnnotation(WithAuthUser.class))
                        .map(WithAuthUser::role))
                .orElse(Roles.SYSTEM_ADMIN);

        CreateUserRequest userRequest = AdminSteps.createTemporaryUser();
        AdminSteps.addRoleForUser(userRequest, role);

        var tokenResponse = UserSteps.createTokenForUser(userRequest);
        AuthUser authUser = new AuthUser(userRequest.getUsername(), userRequest.getPassword(), tokenResponse.getValue());

        threadLocalAuthUser.set(authUser);
        threadLocalUserRequest.set(userRequest);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        CreateUserRequest userRequest = threadLocalUserRequest.get();
        if (userRequest != null) {
            AdminSteps.deleteUser(userRequest);
        }
        threadLocalAuthUser.remove();
        threadLocalUserRequest.remove();
    }

    public static AuthUser getAuthUser() {
        return threadLocalAuthUser.get();
    }
}