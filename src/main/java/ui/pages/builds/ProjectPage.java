package ui.pages.builds;


import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import ui.pages.BasePage;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ProjectPage extends BasePage {

    private final SelenideElement runButton = $("[data-test='run-build']");


    public ProjectPage openProjectById(String projectId) {
        Selenide.open("/project/" + projectId + "?mode=builds");
        $("[data-test='run-build']").shouldBe(visible);
        return this;
    }

    public ProjectPage runBuild() {
        runButton.shouldBe(enabled).click();
        return this;
    }

    //не могу им пользоваться, потому что ссылка этой страницы имеет в себе название проекта
    @Override
    public String url() {
        return "";
    }

}
