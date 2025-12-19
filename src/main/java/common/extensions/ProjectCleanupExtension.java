package common.extensions;

import api.models.project.CreateProjectResponse;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.ArrayList;
import java.util.List;

public class ProjectCleanupExtension implements AfterEachCallback {

    private static final ThreadLocal<List<CreateProjectResponse>> threadLocalProjects = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> threadLocalUiProjects = new ThreadLocal<>();

    @Override
    public void afterEach(ExtensionContext context) {
        List<CreateProjectResponse> projects = threadLocalProjects.get();
        if (projects != null && !projects.isEmpty()) {
            for (CreateProjectResponse project : projects) {
                UserSteps.deleteProjectById(project, RequestSpecs.adminAuthSpec());
            }
            projects.clear();
        }
        threadLocalProjects.remove();
        List<String> uiProjects = threadLocalUiProjects.get();
        if (uiProjects != null && !uiProjects.isEmpty()) {
            for (String projectName : uiProjects) {
                UserSteps.deleteProjectByName(projectName, RequestSpecs.adminAuthSpec());
            }
            uiProjects.clear();
        }
        threadLocalProjects.remove();

    }

    public static void registerProject(CreateProjectResponse project) {
        List<CreateProjectResponse> projects = threadLocalProjects.get();
        if (projects == null) {
            projects = new ArrayList<>();
            threadLocalProjects.set(projects);
        }
        projects.add(project);
    }

    public static void registerUiProject(String projectName) {
        List<String> projects = threadLocalUiProjects.get();
        if (projects == null) {
            projects = new ArrayList<>();
            threadLocalUiProjects.set(projects);
        }
        projects.add(projectName);
    }
}