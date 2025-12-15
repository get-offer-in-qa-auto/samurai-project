package common.extensions;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

public class AdminSessionExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        boolean isUiTest = context.getTestClass()
                .map(clazz -> clazz.getPackageName().startsWith("ui"))
                .orElse(false);

        if (!isUiTest) {
            return; // для не-UI тестов ничего не делаем
        }
        BasePage.authAsSuperUser();
    }
}