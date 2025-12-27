package ui.pages.builds;

import api.models.builds.CreateBuildResponse;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import ui.pages.BasePage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.$;


public class BuildPage extends BasePage {

    private final SelenideElement name = $x("//span[normalize-space()='BuildName']");
    private final SelenideElement title = $$("span").findBy(text("In queue"));
    private final SelenideElement stopButton = $x("//button[@title='Cancel build...']");
    private final SelenideElement modal = $("#stopBuildFormDialog");
    private final SelenideElement removeButtonInModal = $("input[value='Remove']");
    private final SelenideElement cancelledBuildText = $x("//div[normalize-space()='Canceled']");


    public BuildPage findName(CreateBuildResponse createBuildResponse) {
        return StepLogger.log("Поиск имени билда на странице " + createBuildResponse, () -> {
            SelenideElement name = $x("//span[normalize-space()='" + createBuildResponse.getBuildType().getName()
                    + "']]");
            name.shouldBe(visible);
            return this;
        });

    }

    public BuildPage openPage(CreateBuildResponse createBuildResponse) {
        return StepLogger.log("Открытие страницы", () -> {
            Selenide.open("/buildConfiguration/" + createBuildResponse.getBuildTypeId() + "/" + createBuildResponse.getId());
            title.shouldBe(visible);
            return this;
        });
    }


    public BuildPage stopBuild() {
        return StepLogger.log("Поиск кнопки остановки билда и ее нажатие ", () -> {
            stopButton.shouldBe(visible);
            stopButton.click();
            return this;
        });
    }

    public BuildPage findModal() {
        return StepLogger.log("Поиск модального окна на странице ", () -> {
            modal.shouldBe(visible);
            return this;
        });
    }

    public BuildPage cancelBuildFromQueue() {
        return StepLogger.log("Найти кнопку остановки билда и нажать", () -> {
            removeButtonInModal.shouldBe(visible);
            removeButtonInModal.click();
            return this;
        });
    }

    public BuildPage checkIfBuildIsCancelled() {
        return StepLogger.log("Проверка, что билд отменен", () -> {
            cancelledBuildText.shouldBe(visible);
            return this;
        });
    }

    //не могу им пользоваться, тк моя ссылка формируется с помощью айди buildConfiguration + id самого билда
    @Override
    public String url() {
        return "";
    }
}
