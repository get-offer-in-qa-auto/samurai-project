package ui.pages.project;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import ui.pages.BasePage;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Selenide.$;

public class CertainProjectPage extends BasePage<CertainProjectPage> {
    private final String projectName;

    private final SelenideElement projectSettingsButton = $(Selectors.byAttribute("aria-label", "Settings"));

    public CertainProjectPage(String name) {
        projectName = name;
    }

    @Override
    public String url() {
        return "/project/" + URLEncoder.encode(projectName, StandardCharsets.UTF_8);
    }

    public EditProjectPage openProjectSettingsPage() {
        return StepLogger.log("Переход на страницу редактирования проекта", () -> {
            click(projectSettingsButton);
            return new EditProjectPage();
        });
    }

}
