package ui;

import api.BaseTest;
import api.configs.Config;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import common.messages.HasMessage;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.Alert;

import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.switchTo;
import static java.util.Arrays.stream;

public class BaseUiTest extends BaseTest {
    @BeforeAll
    public static void setupSelenoid() {

        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.baseUrl = Config.getProperty("server");
        Configuration.browser = Config.getProperty("browser");
        Configuration.browserSize = Config.getProperty("browserSize");
        Configuration.headless = false;

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }

    private List<String> getUiErrors() {
        ElementsCollection errors = $$x("//span[@data-error]");
        errors.shouldBe(CollectionCondition.sizeGreaterThan(0));

        return errors.stream()
                .map(e -> e.getAttribute("data-error"))
                .toList();
    }

    protected <E extends Enum<E> & HasMessage> void assertUiErrorsMatchExactly(E... expectedErrors) {
        List<String> actualErrors = getUiErrors();

        List<String> expectedMessages = stream(expectedErrors)
                .map(E::getMessage)
                .toList();

        softly.assertThat(actualErrors)
                .as("Проверка UI-ошибок")
                .containsExactlyInAnyOrderElementsOf(expectedMessages);
    }

    protected <E extends Enum<E> & HasMessage> void assertUiErrorsContain(E... expectedErrors) {
        List<String> actualErrors = getUiErrors();

        for (E expected : expectedErrors) {
            softly.assertThat(actualErrors)
                    .as("Проверка UI-ошибки (частичное вхождение): " + expected.name())
                    .anyMatch(error -> error.contains(expected.getMessage()));
        }

    }

    protected <E extends Enum<E> & HasMessage> void checkAlertMessageAndAccept(E alertText) {
        Alert alert = switchTo().alert();
        softly.assertThat(alert.getText())
                .as("Проверка сообщения алерта")
                .contains(alertText.getMessage());
        alert.accept();
    }
}