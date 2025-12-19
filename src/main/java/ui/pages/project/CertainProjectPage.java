package ui.pages.project;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import ui.pages.BasePage;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class CertainProjectPage extends BasePage<CertainProjectPage> {
    private final String projectName;

    protected final SelenideElement projectSettingsButton = $(Selectors.byAttribute("aria-label", "Settings"));

    public CertainProjectPage(String name) {
        projectName = name;
    }

    @Override
    public String url() {
        return "/project/" + projectName;
    }

    public EditProjectPage gotoSettings() {
        projectSettingsButton.shouldBe(visible, enabled).click();
        return new EditProjectPage();

    }
}
