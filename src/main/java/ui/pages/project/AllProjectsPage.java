package ui.pages.project;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import ui.pages.BasePage;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;


public class AllProjectsPage extends BasePage<AllProjectsPage> {
    private final SelenideElement createProjectButton = $(Selectors.byTitle("Create new project"));


    public String url() {
        return "/favorite/projects";
    }

    public CreateProjectPage openCreationProjectPage() {
        return StepLogger.log("Переход на страницу создания проекта", () -> {
            click(createProjectButton);
            return new CreateProjectPage();
        });
    }

    public CertainProjectPage openCertainProject(String name) {
        return StepLogger.log("Открытие проекта: " + name, () -> {
            click($(Selectors.byTitle(name)));
            return new CertainProjectPage(name);
        });
    }

    public boolean projectShouldNotPresent(String name) {
        return StepLogger.log("Проверка отсутствия проекта: " + name, () ->
                $$(Selectors.byTitle(name)).isEmpty()
        );
    }

}
