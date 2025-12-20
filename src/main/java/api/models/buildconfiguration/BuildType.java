package api.models.buildConfiguration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuildType {
    private String id;
    private String name;
    private String projectName;
    private String projectId;
    private String href;
    private String webUrl;
}
