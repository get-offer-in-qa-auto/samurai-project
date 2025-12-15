package ui.pages.users;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ui.pages.BasePage;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class UsersPage extends BasePage<UsersPage> {
    private final SelenideElement buttonCreateUserAccount = $x("//span[contains(text(),'Create user account')]");
    private final SelenideElement searchUsersField = $x("//input[@id='keyword']");
    private final SelenideElement buttonFilter = $x("//input[@name='submitFilter']");
    private final ElementsCollection table = $$x("//td[@class='username highlight']");


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
        table.findBy(text(name)).click();
        return this;
    }

    public List<String> getUsernamesFromTable() {
        return table.stream()
                .map(SelenideElement::getText)
                .toList();
    }
}