package api.models.buildConfiguration;

import api.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateBuildTypeResponse extends BaseModel {
    private String id;
    private String name;
    private String projectName;
    private String projectId;
    private String href;
    private String webUrl;
    private Project project;
    private CountWrapper templates;
    private CountWrapper vcsRootEntries;
    private Settings settings;
    private HrefWrapper parameters;
    private HrefWrapper outputParameters;
    private CountWrapper steps;
    private CountWrapper features;
    private CountWrapper triggers;
    private CountWrapper snapshotDependencies;
    private CountWrapper artifactDependencies;
    private CountWrapper agentRequirements;
    private HrefWrapper builds;
    private HrefWrapper investigations;
    private HrefWrapper compatibleAgents;
    private Object compatibleCloudImages;
}
