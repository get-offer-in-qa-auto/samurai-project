package ui.pages.buildType;

import com.codeborne.selenide.SelenideElement;
import common.messages.BuildTypeErrorMessage;
import ui.pages.BankAlert;
import ui.pages.BasePage;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class BuildTypeEditPage extends BasePage<BuildTypeEditPage> {

    private final SelenideElement nameInput = $("input[name='name']");
    private final SelenideElement descriptionInput = $("textarea[name='description']");
    private final SelenideElement saveButton = $("input[name='submitButton']");
    private final SelenideElement successMessage = $("#unprocessed_buildTypeUpdated");

    private static final String URL_TEMPLATE = "/admin/editBuild.html?id=buildType:%s";

    public String url() {
        return String.format(URL_TEMPLATE, "Test");
    }

    public BuildTypeEditPage editBuildTypeName(String expectedName, String buildTypeNewName) {
        shouldHaveName(expectedName);
        fillName(buildTypeNewName);
        save();

        return this;
    }

    //("Открыть страницу редактирования build configuration с id {buildTypeId}")
    public BuildTypeEditPage openForBuildType(String buildTypeId) {
        return open(String.format(URL_TEMPLATE, buildTypeId));
    }

    //("Проверить имя build configuration: {expectedName}")
    public BuildTypeEditPage shouldHaveName(String expectedName) {
        nameInput
                .shouldBe(visible)
                .shouldHave(value(expectedName));
        return this;
    }

    private void fillName(String name) {
        nameInput.shouldBe(visible, enabled).setValue(name);
    }

    //("Проверка сообщения об успешном изменении")
    public BuildTypeEditPage shouldSeeSuccessMessage() {

        successMessage
                .shouldBe(visible)
                .shouldHave(text(BankAlert.CHANGES_SAVED.getMessage()));
        return this;
    }

    //("Сохранить build configuration")
    public BuildTypeEditPage save() {
        saveButton
                .shouldBe(enabled)
                .click();
        return this;
    }

    public BuildTypeEditPage shouldSeeDuplicateNameError(String buildTypeName) {

        $("body")
                .shouldHave(text(BuildTypeErrorMessage.EDIT_DUPLICATE_NAME.getMessage()));

        return this;
    }
}
