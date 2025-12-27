package ui.pages.builds;


import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import ui.pages.BasePage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class ProjectPage extends BasePage {

    private final SelenideElement runButton = $("[data-test='run-build']");
    private final SelenideElement addedBuild = $x("//div[contains(@class,'ring-message-description') and contains(normalize-space(.),'position in queue')]");

    public ProjectPage openProjectById(String projectId) {
        Selenide.open("/project/" + projectId + "?mode=builds");
        $("[data-test='run-build']").shouldBe(visible);
        return this;
    }

    public ProjectPage runBuild() {
        return StepLogger.log("Запуск билда", ()->{
            runButton.shouldBe(enabled).click();
            return this;
                });
    }

    public ProjectPage checkIfBuildIsAdded(){
        return StepLogger.log("Проверка, что билд запущен", ()->{
            addedBuild
                    .shouldBe(visible);
            return this;
                });
    }

    //не могу им пользоваться, потому что ссылка этой страницы имеет в себе название проекта
    @Override
    public String url() {
        return "";
    }

}
