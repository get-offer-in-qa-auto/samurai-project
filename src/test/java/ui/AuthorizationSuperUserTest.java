package ui;

import api.configs.Config;
import com.codeborne.selenide.Configuration;
import common.helpers.TeamCityTokenUpdater;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ui.pages.authorization.FirstStartPage;
import ui.pages.authorization.SetUpAdminPage;

public class AuthorizationSuperUserTest extends BaseUiTest {

    @BeforeAll
    static void printConfig() {
        System.out.println("=== TEST CONFIG ===");
        System.out.println("SERVER = " + Config.getProperty("server"));
        System.out.println("UIREMOTE = " + Config.getProperty("uiRemote"));

        Configuration.baseUrl = Config.getProperty("server");
        Configuration.remote = Config.getProperty("uiRemote");
    }
    @Test
    public void setUpTeamCity() {
        SetUpAdminPage adminPage = new FirstStartPage()
                .open()
                .clickProceed()
                .clickProceed()
                .clickOnCheckBoxAndAcceptAndGoToSetUpPage();
        adminPage.checkField();
    }

    @AfterEach
    void extractTokenAfterTest() {

        TeamCityTokenUpdater.updateAdminPassword(
                "infra/docker_compose/teamcity_server/logs/teamcity-server.log",
                "src/main/resources/config.properties"
        );
    }
}