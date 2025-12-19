package ui.pages.project;

import api.configs.Config;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import ui.pages.BasePage;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
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

    public EditProjectPage createProjectManually(String name, String id) {
        createProjectManually.shouldBe(visible, enabled).click();
        projectNameInput.shouldBe(visible, enabled).setValue(name);
        idInput.shouldBe(visible, enabled).setValue(id);
        createButton.shouldBe(visible, enabled).click();
        registerUiProject(name);
        return new EditProjectPage();
    }

    public EditProjectPage createProjectFromRepository(String name) {
        createProjectFromARepositoryUrl.shouldBe(visible, enabled).click();
        repositoryUrlInput.shouldBe(visible, enabled).setValue(Config.getProperty("repositoryForProject"));
        proceedButton.shouldBe(visible, enabled).click();
        loader.should(disappear);
        waitForLoader();
        fromRepositoryProjectNameInput.shouldBe(visible, enabled).setValue(name);
        proceedButton.shouldBe(visible, enabled).click();
        registerUiProject(name);
        return new EditProjectPage();
    }

    private void waitForLoader() {
        if (loader.exists()) {
            loader.should(appear, Duration.ofSeconds(5));
            loader.should(disappear, Duration.ofSeconds(15));
        }
    }

    public CreateProjectPage inputData(String name, String id) {
        createProjectManually.shouldBe(visible, enabled).click();
        projectNameInput.shouldBe(visible, enabled).setValue(name);
        idInput.shouldBe(visible, enabled).setValue(id);
        createButton.shouldBe(visible, enabled).click();
        return this;
    }

    public String getUnsuccessIdMessageForManually() {
        return idValidationErrorText.getText();
    }
}
