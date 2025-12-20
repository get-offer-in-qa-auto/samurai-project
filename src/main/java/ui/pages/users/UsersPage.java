package ui.pages.users;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ui.pages.BasePage;

import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class UsersPage extends BasePage<UsersPage> {
    private final SelenideElement buttonCreateUserAccount = $x("//span[contains(text(),'Create user account')]");
    private final SelenideElement searchUsersField = $x("//input[@id='keyword']");
    private final SelenideElement buttonFilter = $x("//input[@name='submitFilter']");
    private final ElementsCollection table = $$x("//td[@class='username highlight']");
    private final SelenideElement contentWrapper = $x("//span[contains(text(),'Users')]");
    private final SelenideElement buttonRemove = $x("//a[contains(text(),'Remove users')]");

    private SelenideElement userCheckboxById(int userId) {
        return $x("//input[@value='" + userId + "']");
    }


    @Override
    public String url() {
        return "/admin/admin.html?item=users";
    }


    public UsersPage findUser(String name) {
        searchUsersField.shouldBe(visible, enabled).setValue(name);
        buttonFilter.click();
        return this;
    }

    public UsersPage openEditPage(String name) {
        table.findBy(text(name)).shouldBe(visible).click();
        return this;
    }

    public List<String> getUsernamesFromTable() {
        table.shouldBe(CollectionCondition.sizeGreaterThan(0));
        return table.stream()
                .map(SelenideElement::getText)
                .toList();
    }

    public boolean viewContentWrapper() {
        return contentWrapper.isDisplayed();
    }

    public UsersPage selectUserById(int userId) {
        userCheckboxById(userId)
                .closest("span")
                .click();
        return this;
    }

    public UsersPage clickButtonRemove() {
        buttonRemove.shouldBe(visible, enabled).click();
        return this;
    }
}