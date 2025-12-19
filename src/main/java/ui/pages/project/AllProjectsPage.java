package ui.pages.project;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import ui.pages.BasePage;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;


public class AllProjectsPage extends BasePage<AllProjectsPage> {
    private final SelenideElement createProjectButton = $(Selectors.byTitle("Create new project"));


    public String url() {
        return "/favorite/projects";
    }

    public CreateProjectPage gotoCreateProject() {
        createProjectButton.shouldBe(visible, enabled).click();
        return new CreateProjectPage();
    }

    public CertainProjectPage gotoProject(String name) {
        $(Selectors.byTitle(name)).shouldBe(visible, enabled).click();
        return new CertainProjectPage(name);
    }

    public boolean projectShouldNotPresent(String name) {
        return $$(Selectors.byTitle(name)).isEmpty();
    }
}
