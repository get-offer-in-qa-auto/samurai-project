package ui.pages.project;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import ui.pages.BasePage;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class EditProjectPage extends BasePage {
    private final SelenideElement messageProjectCreatedManually = $(Selectors.byId("message_projectCreated"));
    private final SelenideElement messageProjectCreatedFromRepository = $(Selectors.byId("unprocessed_objectsCreated"));
    private final SelenideElement actionsPopup = $$(".toolbarItem").first();
    private final SelenideElement deleteButton = $(Selectors.byTitle("Delete project"));
    private final SelenideElement saveButton = $(Selectors.byName("submitButton"));
    private final SelenideElement descriptionInput = $(Selectors.byId("description"));
    private final SelenideElement updateResultMessage = $(Selectors.byId("message_projectUpdated"));


    @Override
    public String url() {
        return "/admin/editProject.html";
    }

    public String getMessageForManually() {
        return messageProjectCreatedManually.getText();
    }

    public String getMessageForFromRepository() {
        return messageProjectCreatedFromRepository.getText();
    }

    public void deleteProject() {
        actionsPopup.shouldBe(visible, enabled).click();
        deleteButton.shouldBe(visible, enabled).click();
    }

    public EditProjectPage updateDescription(String value) {
        descriptionInput.shouldBe(visible, enabled).setValue(value);
        saveButton.shouldBe(visible, enabled).click();
        return this;
    }

    public String getUpdateMessage() {
        return updateResultMessage.shouldBe(visible, enabled).getText();
    }

}
