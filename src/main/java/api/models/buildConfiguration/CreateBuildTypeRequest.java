package api.models.buildConfiguration;

import api.models.BaseModel;
import api.models.project.GetProjectsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateBuildTypeRequest extends BaseModel {
        private String name;
        private Project project;
}
