package api.models.buildConfiguration;

import api.models.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBuildTypeResponse extends BaseModel {
    private String id;
    private String name;
    private String projectName;
    private String projectId;
    private Project project;
}
