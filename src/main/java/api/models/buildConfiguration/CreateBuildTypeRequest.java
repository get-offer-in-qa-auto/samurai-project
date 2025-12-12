package api.models.buildConfiguration;

import api.generators.GeneratingRule;
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
        @GeneratingRule(regex = "^[A-Za-z0-9]{3,15}$")
        private String name;

        private Project project;
}
