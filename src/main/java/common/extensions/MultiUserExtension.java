package common.extensions;

import common.annotations.WithAuthUser;
import common.storage.UserSessionStore;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Optional;

public class MultiUserExtension implements BeforeEachCallback, AfterEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) {
        Optional<WithAuthUser> annotation = context.getTestMethod()
                .flatMap(m -> Optional.ofNullable(m.getAnnotation(WithAuthUser.class)))
                .or(() -> context.getTestClass()
                        .flatMap(c -> Optional.ofNullable(c.getAnnotation(WithAuthUser.class))));

        annotation.ifPresent(withUsers -> {
            for (int i = 0; i < withUsers.count(); i++) {
                UserSessionStore.create(withUsers.role());
            }
        });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        UserSessionStore.cleanup();
    }
}