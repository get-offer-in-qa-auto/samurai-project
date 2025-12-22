package ui.pages.builds;

import api.models.builds.CreateBuildResponse;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import ui.pages.BasePage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;


public class BuildPage extends BasePage {

    private final SelenideElement name = $x("//span[normalize-space()='BuildName']");
    private final SelenideElement title = $$("span").findBy(text("In queue"));
    private final SelenideElement stopButton = $x("//button[@title='Cancel build...']");
    private final SelenideElement modal = $("#stopBuildFormDialog");
    private final SelenideElement removeButtonInModal = $("input[value='Remove']");

    public BuildPage findName(CreateBuildResponse createBuildResponse) {
        SelenideElement name = $x("//span[normalize-space()='" + createBuildResponse.getBuildType().getName()
                + "']]");
        name.shouldBe(visible);
        return this;
    }

    public BuildPage openPage(CreateBuildResponse createBuildResponse) {
        Selenide.open("/buildConfiguration/" + createBuildResponse.getBuildTypeId() + "/" + createBuildResponse.getId());
        title.shouldBe(visible);
        return this;
    }


    public BuildPage stopBuild() {
        stopButton.shouldBe(visible);
        stopButton.click();
        return this;
    }

    public BuildPage findModal() {
        modal.shouldBe(visible);
        return this;
    }

    public BuildPage cancelBuildFromQueue() {
        removeButtonInModal.shouldBe(visible);
        removeButtonInModal.click();
        return this;
    }

    //не могу им пользоваться, тк моя ссылка формируется с помощью айди buildConfiguration + id самого билда
    @Override
    public String url() {
        return "";
    }
}
