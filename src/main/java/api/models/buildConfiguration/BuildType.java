package api.models.buildConfiguration;

import lombok.Data;

@Data
public class BuildType {
    private String id;
    private String name;
    private String projectName;
    private String projectId;
    private String href;
    private String webUrl;
}
