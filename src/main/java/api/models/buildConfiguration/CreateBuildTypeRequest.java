package api.models.buildConfiguration;

import api.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBuildTypeRequest extends BaseModel {
        private String name;
        private Project project;
}
