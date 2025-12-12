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

    @Override
    public void afterEach(ExtensionContext context) {
        List<CreateProjectResponse> projects = threadLocalProjects.get();
        if (projects != null && !projects.isEmpty()) {
            for (CreateProjectResponse project : projects) {
                UserSteps.deleteProject(project, RequestSpecs.adminAuthSpec());
            }
            projects.clear();
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
}