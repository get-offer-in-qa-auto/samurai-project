package common.helpers;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;

public class StepLogger {

    @FunctionalInterface
    public interface ThrowableRunnable<T> {
        T run() throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowableVoidRunnable {
        void run() throws Throwable;
    }

    public static <T> T log(String title, ThrowableRunnable<T> runnable) {
        return Allure.step(title, () -> {
            T result = runnable.run();
            attachScreenshot();
            return result;
        });
    }

    public static void log(String title, ThrowableVoidRunnable runnable) {
        Allure.step(title, () -> {
            runnable.run();
            attachScreenshot();
            return null;
        });
    }

    private static void attachScreenshot() {
        // если вебдрайвер не запущен (API- шаг) — просто выходим
        try {
            var driver = WebDriverRunner.getWebDriver();
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot", "image/png",
                    new ByteArrayInputStream(bytes), ".png");
        } catch (IllegalStateException e) {
            System.err.println("[StepLogger] Не удалось сделать скриншот: " + e.getMessage());
        }
    }
}
