package ui.pages;

import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.refresh;

public abstract class BasePage<T extends BasePage> {
    public abstract String url();

    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public T open(String url) {
        return Selenide.open(url, (Class<T>) this.getClass());
    }

    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }

    public static void authAsSuperUser() {
        Selenide.open("/");
        String sessionId = AdminSteps.getAdminSessionId();
        WebDriverRunner.getWebDriver()
                .manage()
                .addCookie(new Cookie.Builder("TCSESSIONID", sessionId)
                        .path("/")
                        .build());
        refresh();
    }

    protected void click(SelenideElement element) {
        element.shouldBe(visible, enabled).click();
    }
    protected void setValue(SelenideElement element, String value) {
        element.shouldBe(visible, enabled).setValue(value);
    }
}