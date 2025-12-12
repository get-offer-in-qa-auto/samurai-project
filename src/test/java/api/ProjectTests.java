package api;

import api.comparison.ModelAssertions;
import api.generators.RandomModelGenerator;
import api.models.error.ErrorResponse;
import api.models.project.CreateProjectFromRepositoryRequest;
import api.models.project.CreateProjectManuallyRequest;
import api.models.project.CreateProjectResponse;
import api.models.project.GetProjectsResponse;
import api.models.project.GetProjectsResponse.Project;
import api.models.users.Roles;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.WithAuthUser;
import common.errors.ProjectErrorMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static api.generators.RandomData.getProjectId;
import static api.generators.RandomData.getProjectName;

public class ProjectTests extends BaseTest {

    public static Stream<Arguments> validProjectIds() {
        return Stream.of(
                //позитивные граничные значения
                Arguments.of(getProjectId(1)),
                Arguments.of(getProjectId(225))
        );
    }

    public static Stream<Arguments> invalidProjectIds() {
        return Stream.of(
                //негативные граничные значения
                Arguments.of("", ProjectErrorMessage.PROJECT_ID_EMPTY),
                Arguments.of(getProjectId(226), ProjectErrorMessage.PROJECT_ID_TOO_LONG),
                //начинающийся с underscore
                Arguments.of("_id", ProjectErrorMessage.PROJECT_ID_STARTS_WITH_UNDERSCORE),
                //сод-ся символы кроме _
                Arguments.of("i-d", ProjectErrorMessage.PROJECT_ID_UNSUPPORTED_CHARACTER),
                //сод-ся не латинская буква,
                Arguments.of("iд", ProjectErrorMessage.PROJECT_ID_NON_LATIN),
                //начинающийся с цифры
                Arguments.of("1d", ProjectErrorMessage.PROJECT_ID_STARTS_WITH_DIGIT)
        );
    }

    @ParameterizedTest
    @MethodSource("validProjectIds")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanCreateProjectManually(String id) {
        int initialCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());

        CreateProjectFromRepositoryRequest requestModel = RandomModelGenerator.generate(CreateProjectFromRepositoryRequest.class);
        requestModel.setId(id);

        CreateProjectResponse response = new ValidatedCrudRequester<CreateProjectResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_CREATE_MANUALLY,
                ResponseSpecs.requestReturnsOK())
                .post(requestModel);

        ModelAssertions.assertThatModels(requestModel, response).match();

        int finalCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(finalCount).isEqualTo(initialCount + 1);
    }


    @ParameterizedTest
    @MethodSource("validProjectIds")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanCreateProjectFromRepository(String id) {
        int initialCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());

        CreateProjectFromRepositoryRequest requestModel = RandomModelGenerator.generate(CreateProjectFromRepositoryRequest.class);
        requestModel.setId(id);

        CreateProjectResponse response = new ValidatedCrudRequester<CreateProjectResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_CREATE_FROM_REPOSITORY,
                ResponseSpecs.requestReturnsOK())
                .post(requestModel);

        ModelAssertions.assertThatModels(requestModel, response).match();

        int finalCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(finalCount).isEqualTo(initialCount + 1);
    }


    @ParameterizedTest
    @MethodSource("invalidProjectIds")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCannotCreateProjectManuallyWithInvalidData(String id, ProjectErrorMessage expectedError) {
        int initialCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());

        CreateProjectFromRepositoryRequest requestModel = RandomModelGenerator.generate(CreateProjectFromRepositoryRequest.class);
        requestModel.setId(id);

        var projectCreationResponse = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_CREATE_FROM_REPOSITORY,
                ResponseSpecs.requestReturnsInternalServerError())
                .post(requestModel);

        int finalCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(finalCount).isEqualTo(initialCount);
        ErrorResponse errorResponse = extractError(projectCreationResponse);
        assertErrorMessageContains(errorResponse, expectedError);
    }


    @ParameterizedTest
    @MethodSource("invalidProjectIds")
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCannotCreateProjectFromRepositoryWithInvalidData(String id, ProjectErrorMessage expectedError) {
        int initialCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());

        CreateProjectFromRepositoryRequest requestModel = RandomModelGenerator.generate(CreateProjectFromRepositoryRequest.class);
        requestModel.setId(id);

        var projectCreationResponse = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_CREATE_FROM_REPOSITORY,
                ResponseSpecs.requestReturnsInternalServerError())
                .post(requestModel);

        int finalCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(finalCount).isEqualTo(initialCount);
        ErrorResponse errorResponse = extractError(projectCreationResponse);
        assertErrorMessageContains(errorResponse, expectedError);
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCannotCreateProjectManuallyWithEmptyName() {
        int initialCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());

        CreateProjectManuallyRequest requestModel = RandomModelGenerator.generate(CreateProjectManuallyRequest.class);
        requestModel.setName("");

        var projectCreationResponse = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_CREATE_MANUALLY,
                ResponseSpecs.requestReturns400BadRequest())
                .post(requestModel);

        int finalCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(finalCount).isEqualTo(initialCount);
        ErrorResponse errorResponse = extractError(projectCreationResponse);
        assertErrorMessageContains(errorResponse, ProjectErrorMessage.PROJECT_NAME_EMPTY);
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCannotCreateProjectFromRepositoryWithEmptyName() {
        int initialCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());

        CreateProjectFromRepositoryRequest requestModel = RandomModelGenerator.generate(CreateProjectFromRepositoryRequest.class);
        requestModel.setName("");

        var projectCreationResponse = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_CREATE_FROM_REPOSITORY,
                ResponseSpecs.requestReturns400BadRequest())
                .post(requestModel);

        int finalCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(finalCount).isEqualTo(initialCount);
        ErrorResponse errorResponse = extractError(projectCreationResponse);
        assertErrorMessageContains(errorResponse, ProjectErrorMessage.PROJECT_NAME_EMPTY);
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCannotCreateProjectManuallyWithExistingId() {
        int initialCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());

        CreateProjectManuallyRequest firstProject = RandomModelGenerator.generate(CreateProjectManuallyRequest.class);

        new ValidatedCrudRequester<CreateProjectResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_CREATE_MANUALLY,
                ResponseSpecs.requestReturnsOK())
                .post(firstProject);

        CreateProjectManuallyRequest secondProject = RandomModelGenerator.generate(CreateProjectManuallyRequest.class);
        secondProject.setId(firstProject.getId());

        var projectCreationResponse = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_CREATE_MANUALLY,
                ResponseSpecs.requestReturns400BadRequest())
                .post(secondProject);

        int finalCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(finalCount).isEqualTo(initialCount + 1);
        ErrorResponse errorResponse = extractError(projectCreationResponse);
        assertErrorMessageContains(errorResponse, ProjectErrorMessage.PROJECT_ID_ALREADY_USED);
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCannotCreateProjectFromRepositoryWithExistingId() {
        int initialCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());

        CreateProjectFromRepositoryRequest firstProject = RandomModelGenerator.generate(CreateProjectFromRepositoryRequest.class);

        new ValidatedCrudRequester<CreateProjectResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_CREATE_FROM_REPOSITORY,
                ResponseSpecs.requestReturnsOK())
                .post(firstProject);

        CreateProjectFromRepositoryRequest secondProject = RandomModelGenerator.generate(CreateProjectFromRepositoryRequest.class);
        secondProject.setId(firstProject.getId());

        var projectCreationResponse = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_CREATE_FROM_REPOSITORY,
                ResponseSpecs.requestReturns400BadRequest())
                .post(secondProject);

        int finalCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(finalCount).isEqualTo(initialCount + 1);
        ErrorResponse errorResponse = extractError(projectCreationResponse);
        assertErrorMessageContains(errorResponse, ProjectErrorMessage.PROJECT_ID_ALREADY_USED);
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCannotCreateProjectManuallyWithExistingName() {
        int initialCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());

        CreateProjectManuallyRequest firstProject = RandomModelGenerator.generate(CreateProjectManuallyRequest.class);

        new ValidatedCrudRequester<CreateProjectResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_CREATE_MANUALLY,
                ResponseSpecs.requestReturnsOK())
                .post(firstProject);

        CreateProjectManuallyRequest secondProject = RandomModelGenerator.generate(CreateProjectManuallyRequest.class);
        secondProject.setName(firstProject.getName());

        var projectCreationResponse = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_CREATE_MANUALLY,
                ResponseSpecs.requestReturns400BadRequest())
                .post(secondProject);

        int finalCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(finalCount).isEqualTo(initialCount + 1);
        ErrorResponse errorResponse = extractError(projectCreationResponse);
        assertErrorMessageContains(errorResponse, ProjectErrorMessage.PROJECT_NAME_EXISTS);
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCannotCreateProjectFromRepositoryWithExistingName() {
        int initialCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());

        CreateProjectFromRepositoryRequest firstProject = RandomModelGenerator.generate(CreateProjectFromRepositoryRequest.class);

        new ValidatedCrudRequester<CreateProjectResponse>(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_CREATE_FROM_REPOSITORY,
                ResponseSpecs.requestReturnsOK())
                .post(firstProject);

        CreateProjectFromRepositoryRequest secondProject = RandomModelGenerator.generate(CreateProjectFromRepositoryRequest.class);
        secondProject.setName(firstProject.getName());

        var projectCreationResponse = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_CREATE_FROM_REPOSITORY,
                ResponseSpecs.requestReturns400BadRequest())
                .post(secondProject);

        int finalCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(finalCount).isEqualTo(initialCount + 1);
        ErrorResponse errorResponse = extractError(projectCreationResponse);
        assertErrorMessageContains(errorResponse, ProjectErrorMessage.PROJECT_NAME_EXISTS);
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanUpdateProjectDescription() {
        CreateProjectResponse project = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());
        String projectBefore = UserSteps.getProjectById(project.getId(), RequestSpecs.userAuthSpecWithToken())
                .getDescription();
        var randomText = getProjectName();
        var response = new ValidatedCrudRequester<>(
                RequestSpecs.userAuthTextSpecWithToken(),
                Endpoint.PROJECT_UPDATE,
                ResponseSpecs.requestReturnsOK())
                .put(project.id, randomText);
        softly.assertThat(response).isEqualTo(randomText);

        String projectAfter = UserSteps.getProjectById(project.getId(), RequestSpecs.userAuthSpecWithToken())
                .getDescription();
        softly.assertThat(projectAfter).isNotEqualTo(projectBefore);
        softly.assertThat(projectAfter).isEqualTo(randomText);
    }


    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanGetProjectById() {
        CreateProjectResponse createdProject = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());
        Project gettedProject = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.GET_PROJECT_BY_ID,
                ResponseSpecs.requestReturnsOK())
                .get(createdProject.id)
                .extract()
                .as(GetProjectsResponse.class)
                .getProject()
                .getFirst();

        ModelAssertions.assertThatModels(createdProject, gettedProject).match();
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCannotGetProjectWithInvalidId() {
        int matchedCount = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.GET_PROJECT_BY_ID,
                ResponseSpecs.requestReturnsOK())
                .get(getProjectName())
                .extract()
                .as(GetProjectsResponse.class)
                .getCount();

        softly.assertThat(matchedCount).isEqualTo(0);
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCanDeleteProject() {
        CreateProjectResponse project = UserSteps.createProjectManually(RequestSpecs.userAuthSpecWithToken());
        int initialCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());

        new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_DELETE,
                ResponseSpecs.requestReturnsNoContent())
                .delete(project.getId());

        int finalCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(finalCount).isEqualTo(initialCount - 1);
    }

    @Test
    @WithAuthUser(role = Roles.AGENT_MANAGER)
    public void userCannotDeleteProjectWithInvalidId() {
        int initialCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());

        var projectDelResponse = new CrudRequester(
                RequestSpecs.userAuthSpecWithToken(),
                Endpoint.PROJECT_DELETE,
                ResponseSpecs.requestReturns404NotFound())
                .delete(getProjectName());

        int finalCount = UserSteps.getProjectsCount(RequestSpecs.userAuthSpecWithToken());
        softly.assertThat(finalCount).isEqualTo(initialCount);
        ErrorResponse errorResponse = extractError(projectDelResponse);
        assertErrorMessageContains(errorResponse, ProjectErrorMessage.PROJECT_NOT_FOUND);
    }
}
