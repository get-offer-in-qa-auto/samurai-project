package ui.pages.authorization;

import com.codeborne.selenide.SelenideElement;
import ui.pages.BasePage;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class FirstStartPage extends BasePage<FirstStartPage> {
    private final SelenideElement proceedButton = $x("//input[@id='proceedButton']");
    private final SelenideElement checkboxAgreement = $x("//input[@id='licenseAgreementConsent']");
    private final SelenideElement acceptButton = $x("//input[@id='acceptLicenseAgreement']");


    @Override
    public String url() {
        return "/mnt";
    }

    public FirstStartPage clickProceed() {
        proceedButton.shouldBe(visible, Duration.ofMinutes(5)).click();
        return this;
    }

    public SetUpAdminPage clickOnCheckBoxAndAcceptAndGoToSetUpPage() {
        checkboxAgreement.shouldBe(visible, Duration.ofMinutes(5)).scrollTo().click();
        acceptButton.shouldBe(visible, Duration.ofMinutes(5)).scrollTo().click();
        return getPage(SetUpAdminPage.class);
    }
}
