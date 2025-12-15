package ui;

import api.BaseTest;
import api.configs.Config;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import common.errors.UserUiAlertMessage;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$$x;

public class BaseUiTest extends BaseTest {
    @BeforeAll
    public static void setupSelenoid() {
        //настроить Property
//        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.baseUrl = Config.getProperty("server");
        Configuration.browser = Config.getProperty("browser");
        Configuration.browserSize = Config.getProperty("browserSize");

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }

    protected void assertUiErrorsContain(UserUiAlertMessage... expectedErrors) {
        List<String> actualErrors = $$x("//span[@data-error]")
                .stream()
                .map(e -> e.getAttribute("data-error"))
                .toList();

        for (UserUiAlertMessage expected : expectedErrors) {
            softly.assertThat(actualErrors)
                    .as("Проверка UI-ошибки: " + expected.name())
                    .containsExactly(expected.getMessage());
        }
    }
}