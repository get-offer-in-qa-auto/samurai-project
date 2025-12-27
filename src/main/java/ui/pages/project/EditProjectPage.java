package ui.pages.project;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
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
        return StepLogger.log("Получение сообщения о создании проекта вручную", messageProjectCreatedManually::getText
        );
    }

    public String getMessageForFromRepository() {
        return StepLogger.log("Получение сообщения о создании проекта из репозитория", messageProjectCreatedFromRepository::getText
        );
    }

    private void openActionsPopup() {
        StepLogger.log("Нажатие на кнопку открытия меню действий проекта", () ->
                click(actionsPopup)
        );
    }

    private void clickDeleteButton() {
        StepLogger.log("Нажатие на кнопку удаления проекта", () ->
                click(deleteButton)
        );
    }

    private void setDescription(String value) {
        StepLogger.log("Ввод описания проекта: " + value, () ->
                setValue(descriptionInput, value)
        );
    }

    private void clickSaveButton() {
        StepLogger.log("Нажатие на кнопку сохранения проекта", () ->
                click(saveButton)
        );
    }

    public void deleteProject() {
        openActionsPopup();
        clickDeleteButton();
    }

    public EditProjectPage updateDescription(String value) {
        setDescription(value);
        clickSaveButton();
        return this;
    }

    public String getUpdateMessage() {
        return StepLogger.log("Получение сообщения об успешном обновлении проекта", () ->
                updateResultMessage.shouldBe(visible, enabled).getText()
        );
    }

}
