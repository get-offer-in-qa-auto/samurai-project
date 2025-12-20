package api.users;

import api.BaseTest;
import api.generators.RandomData;
import api.models.error.ErrorResponse;
import api.models.users.AuthUser;
import api.models.users.CreateUserRoleRequest;
import api.models.users.CreateUserRoleResponse;
import api.models.users.Roles;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.WithAuthUser;
import common.messages.UserErrorMessage;
import common.extensions.AuthUserExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

public class AssignUserRoleTest extends BaseTest {
    @ParameterizedTest
    @DisplayName("Успешное добавление роли пользователю")
    @EnumSource(
            value = Roles.class,
            names = "SYSTEM_ADMIN",
            mode = EnumSource.Mode.EXCLUDE
    )
    @WithAuthUser
    public void successfullyAddRoleWithValidData(Roles roles) {
        AuthUser authUser = AuthUserExtension.getAuthUser();
        CreateUserRoleRequest request = CreateUserRoleRequest.builder()
                .roleId(roles.name())
                .scope("p:_Root")
                .build();
        CreateUserRoleResponse userRoleResponse = new ValidatedCrudRequester<CreateUserRoleResponse>(
                RequestSpecs.adminAuthSpec(), Endpoint.USER_CREATE_ROLE, ResponseSpecs.requestReturnsOK())
                .post(request, authUser.getId());

        List<CreateUserRoleResponse> roleUser = AdminSteps.getRoleUser(authUser.getId());

        softly.assertThat(roles.name())
                .as("Проверка роли пользователя в ответе")
                .isEqualTo(userRoleResponse.getRoleId());
        softly.assertThat(roleUser).as("Проверка роли пользователя")
                .extracting(CreateUserRoleResponse::getRoleId)
                .containsOnlyOnce(roles.name());
    }

    @Test
    @DisplayName("Неуспешное добавление роли пользователю, пользователь не существует")
    public void unsuccessfullyAddRoleWithInvalidId() {
        CreateUserRoleRequest request = CreateUserRoleRequest.builder()
                .roleId(Roles.USER_ROLE.name())
                .scope("p:_Root")
                .build();
        var userRoleResponse = new CrudRequester(
                RequestSpecs.adminAuthSpec(), Endpoint.USER_CREATE_ROLE, ResponseSpecs.requestReturns404NotFound())
                .post(request, RandomData.getId());
        ErrorResponse errorResponse = extractError(userRoleResponse);

        assertErrorMessage(errorResponse, UserErrorMessage.USER_NOT_FOUND);
    }
}