package ui;

import common.helpers.TeamCityTokenUpdater;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ui.pages.authorization.FirstStartPage;
import ui.pages.authorization.SetUpAdminPage;

public class AuthorizationSuperUserTest extends BaseUiTest {
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