package ui;

import api.BaseTest;
import api.configs.Config;
import com.codeborne.selenide.Configuration;
import common.messages.UserUiAlertMessage;
import org.junit.jupiter.api.BeforeAll;

import java.util.Arrays;
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

    private List<String> getUiErrors() {
        return $$x("//span[@data-error]")
                .stream()
                .map(e -> e.getAttribute("data-error"))
                .toList();
    }

    protected void assertUiErrorsMatchExactly(UserUiAlertMessage... expectedErrors) {
        List<String> actualErrors = getUiErrors();

        List<String> expectedMessages = Arrays.stream(expectedErrors)
                .map(UserUiAlertMessage::getMessage)
                .toList();

        softly.assertThat(actualErrors)
                .as("Проверка UI-ошибок")
                .containsExactlyInAnyOrderElementsOf(expectedMessages);
    }

    protected void assertUiErrorsContain(UserUiAlertMessage... expectedErrors) {
        List<String> actualErrors = getUiErrors();

        for (UserUiAlertMessage expected : expectedErrors) {
            softly.assertThat(actualErrors)
                    .as("Проверка UI-ошибки (частичное вхождение): " + expected.name())
                    .anyMatch(error -> error.contains(expected.getMessage()));
        }
    }
}