package ui.pages.project;

import api.configs.Config;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import ui.pages.BasePage;

import java.time.Duration;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Selenide.$;
import static common.extensions.ProjectCleanupExtension.registerUiProject;

public class CreateProjectPage extends BasePage<CreateProjectPage> {
    private final SelenideElement createProjectManually = $(Selectors.byAttribute("href", "#createManually"));
    private final SelenideElement createProjectFromARepositoryUrl = $(Selectors.byAttribute("href", "#createFromUrl"));
    private final SelenideElement proceedButton = $(Selectors.byAttribute("value", "Proceed"));
    private final SelenideElement projectNameInput = $(Selectors.byId("name"));
    private final SelenideElement fromRepositoryProjectNameInput = $(Selectors.byId("projectName"));
    private final SelenideElement idInput = $(Selectors.byId("externalId"));
    private final SelenideElement repositoryUrlInput = $(Selectors.byId("url"));
    private final SelenideElement createButton = $(Selectors.byId("createProject"));
    private final SelenideElement loader = $("#saving");
    private final SelenideElement idValidationErrorText = $(Selectors.byId("errorExternalId"));


    @Override
    public String url() {
        return "/admin/createObjectMenu.html";
    }

    private void clickCreateProjectManually() {
        click(createProjectManually);
    }

    private void clickCreateProjectFromRepository() {
        click(createProjectFromARepositoryUrl);
    }

    private void setProjectName(String name) {
        setValue(projectNameInput, name);
    }

    private void setProjectId(String id) {
        setValue(idInput, id);
    }

    private void setRepositoryUrl(String url) {
        setValue(repositoryUrlInput, url);
    }

    private void setFromRepositoryProjectName(String name) {
        setValue(fromRepositoryProjectNameInput, name);
    }

    private void clickCreateButton() {
        click(createButton);
    }

    private void clickProceedButton() {
        click(proceedButton);
    }

    private void waitForLoaderIfExists() {
        if (loader.exists()) {
            loader.should(appear, Duration.ofSeconds(5));
            loader.should(disappear, Duration.ofSeconds(15));
        }
    }

    private void registerProject(String name) {
        registerUiProject(name);
    }

    public EditProjectPage createProjectManually(String name, String id) {
        clickCreateProjectManually();
        setProjectName(name);
        setProjectId(id);
        clickCreateButton();
        registerProject(name);
        return new EditProjectPage();
    }

    public EditProjectPage createProjectFromRepository(String name) {
        clickCreateProjectFromRepository();
        setRepositoryUrl(Config.getProperty("repositoryForProject"));
        clickProceedButton();
        waitForLoaderIfExists();
        setFromRepositoryProjectName(name);
        clickProceedButton();
        registerProject(name);
        return new EditProjectPage();
    }

    public CreateProjectPage inputData(String name, String id) {
        clickCreateProjectManually();
        setProjectName(name);
        setProjectId(id);
        clickCreateButton();
        return this;
    }

    public String getUnsuccessIdMessageForManually() {
        return idValidationErrorText.getText();
    }
}
