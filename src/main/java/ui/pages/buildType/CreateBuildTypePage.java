package ui.pages.buildType;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import common.messages.BuildTypeErrorMessage;
import ui.pages.BankAlert;
import ui.pages.BasePage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class CreateBuildTypePage extends BasePage<CreateBuildTypePage> {
    private final SelenideElement manuallyButton = $("a[data-url*='createBuildType.html']");
    private final SelenideElement inputBuildTypeName = $(Selectors.byAttribute("id", "buildTypeName"));
    private final SelenideElement createButton = $("input[name='createBuildType']");
    private final SelenideElement successMessage = $("#unprocessed_buildTypeCreated");
    private final SelenideElement buildTypeExternalIdInput = $("#buildTypeExternalId");


    private static final String URL_TEMPLATE =
            "/admin/createObjectMenu.html?projectId=%s&showMode=createBuildTypeMenu";

    public String url() {
        return String.format(URL_TEMPLATE, "Test");
    }

    public CreateBuildTypePage openForProject(String projectId) {
        return open(String.format(URL_TEMPLATE, projectId));
    }

    public CreateBuildTypePage createBuildTypeManually(String buildTypeName) {

        clickManually();
        fillName(buildTypeName);
        clickCreate();

        return this;
    }

    public String createBuildTypeManuallyAndGetId(String buildTypeName) {

        clickManually();
        fillName(buildTypeName);
        String buildTypeId = getGeneratedBuildTypeExternalId();
        clickCreate();

        return buildTypeId;
    }

    //("Нажать кнопку Manually")
    private void clickManually() {
        manuallyButton.shouldBe(visible, enabled).click();
    }

    //("Ввести имя build configuration: {name}")
    private void fillName(String name) {
        inputBuildTypeName.shouldBe(visible, enabled).setValue(name);
    }

    //("Нажать кнопку Create")
    private void clickCreate() {
        createButton.shouldBe(visible, enabled).click();
    }

    //("Проверить, что build configuration успешно создан")
    public CreateBuildTypePage shouldSeeSuccessMessage() {

        successMessage
                .shouldBe(visible)
                .shouldHave(text(BankAlert.BUILD_CONFIGURATION_CREATE_SUCCESSFULLY.getMessage()
                ));

        return this;
    }

    //("Дождаться генерации Build Configuration ID и получить его")
    public String getGeneratedBuildTypeExternalId() {
        return buildTypeExternalIdInput
                .shouldBe(visible)
                .getValue();
    }

    public CreateBuildTypePage shouldSeeDuplicateNameError(String buildTypeName) {

        $("body")
                .shouldHave(text(BuildTypeErrorMessage.CREATE_DUPLICATE_NAME.getMessage()))
                .shouldHave(text("\"" + buildTypeName + "\""))
                .shouldHave(text("already exists"));

        return this;
    }
}
