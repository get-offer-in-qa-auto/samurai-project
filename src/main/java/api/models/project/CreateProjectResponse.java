package api.models.project;

import api.models.BaseModel;
import lombok.Data;

import java.util.List;

@Data
public class CreateProjectResponse extends BaseModel {

    public String id;
    public String name;
    public String parentProjectId;
    public boolean virtual;
    public String href;
    public String webUrl;
    public ParentProject parentProject;
    public BuildTypes buildTypes;
    public Templates templates;
    public DeploymentDashboards deploymentDashboards;
    public Parameters parameters;
    public VcsRoots vcsRoots;
    public ProjectFeatures projectFeatures;
    public Projects projects;

    public static class ParentProject {
        public String id;
        public String name;
        public String description;
        public String href;
        public String webUrl;
    }

    public static class BuildTypes {
        public int count;
        public List<Object> buildType;
    }

    public static class Templates {
        public int count;
        public List<Object> buildType;
    }

    public static class DeploymentDashboards {
        public int count;
    }

    public static class Parameters {
        public int count;
        public String href;
        public List<Object> property;
    }

    public static class VcsRoots {
        public int count;
        public String href;
    }

    public static class ProjectFeatures {
        public int count;
        public String href;
    }

    public static class Projects {
        public int count;
    }
}