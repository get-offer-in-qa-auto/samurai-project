package common.extensions;

import api.models.users.AuthUser;
import api.models.users.CreateUserRequest;
import api.models.users.CreateUserRoleRequest;
import api.models.users.Roles;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import com.codeborne.selenide.WebDriverRunner;
import common.annotations.WithAuthUser;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.refresh;

public class AuthUserExtension implements BeforeEachCallback, AfterEachCallback {

    private static final ThreadLocal<AuthUser> threadLocalAuthUser = new ThreadLocal<>();
    private static final ThreadLocal<CreateUserRequest> threadLocalUserRequest = new ThreadLocal<>();

    @Override
    public void beforeEach(ExtensionContext context) {
        var annotation = context.getTestMethod()
                .flatMap(method -> java.util.Optional.ofNullable(method.getAnnotation(WithAuthUser.class)))
                .or(() -> context.getTestClass()
                        .flatMap(clazz -> java.util.Optional.ofNullable(clazz.getAnnotation(WithAuthUser.class))));

        if (annotation.isEmpty()) {
            return;
        }
        Roles role = annotation.map(WithAuthUser::role).orElse(Roles.USER_ROLE);

        CreateUserRequest userRequest = AdminSteps.createTemporaryUser();
        CreateUserRoleRequest userRole = AdminSteps.addRoleForUser(userRequest, role);


        var tokenResponse = UserSteps.createTokenForUser(userRequest);
        String sessionId = UserSteps.getUserSessionIdByToken(
                userRequest.getUsername(),
                userRequest.getPassword()
        );
        AuthUser authUser = new AuthUser(userRequest.getUsername(), userRequest.getPassword(),
                tokenResponse.getValue(), userRequest.getId(), userRole.getRoleId(), sessionId);


        threadLocalAuthUser.set(authUser);
        threadLocalUserRequest.set(userRequest);

        setupBrowserSessionIfUiTest(context, sessionId);

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

    private void setupBrowserSessionIfUiTest(ExtensionContext context, String sessionId) {
        boolean isUiTest = context.getTestClass()
                .map(clazz -> clazz.getPackageName().startsWith("ui"))
                .orElse(false);

        if (isUiTest) {
            try {
                open("/");
                if (WebDriverRunner.hasWebDriverStarted()) {
                    WebDriverRunner.getWebDriver()
                            .manage()
                            .addCookie(new Cookie.Builder("TCSESSIONID", sessionId)
                                    .path("/")
                                    .build());
                    refresh();
                }
            } catch (Exception e) {
                System.err.println("Ошибка: Нет сессионного куки  в beforeEach " + e.getMessage());
            }
        }
    }
}
