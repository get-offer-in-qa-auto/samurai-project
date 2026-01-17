package api;

import api.models.error.ErrorResponse;
import common.extensions.MultiUserExtension;
import common.messages.HasMessage;
import common.extensions.AgentExtension;
import common.extensions.AuthUserExtension;
import common.extensions.ProjectCleanupExtension;
import common.messages.HasMessage;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(MultiUserExtension.class)
@ExtendWith(AuthUserExtension.class)
@ExtendWith(AgentExtension.class)
@ExtendWith({AuthUserExtension.class, ProjectCleanupExtension.class})
public class BaseTest {
    protected SoftAssertions softly;

    @BeforeEach
    public void setupTest() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void afterTest() {
        softly.assertAll();
    }

    protected ErrorResponse extractError(ValidatableResponse response) {
        return response.extract().as(ErrorResponse.class);
    }

    protected <E extends Enum<E> & HasMessage> void assertErrorMessage(
            ErrorResponse errorResponse, E expected) {
        softly.assertThat(errorResponse.getErrors().getFirst().getMessage())
                .as("Проверка сообщения алерта")
                .isEqualTo(expected.getMessage());
    }

    protected <E extends Enum<E> & HasMessage> void assertErrorMessageContains(
            ErrorResponse errorResponse, E expected) {
        softly.assertThat(errorResponse.getErrors().getFirst().getMessage())
                .as("Проверка, что сообщение алерта содержит ожидаемую подстроку")
                .contains(expected.getMessage());
    }

    protected void assertErrorMessage(ErrorResponse errorResponse, String expectedMessage) {
        softly.assertThat(errorResponse.getErrors().getFirst().getMessage())
                .as("Проверка сообщения алерта")
                .isEqualTo(expectedMessage);
    }
}
