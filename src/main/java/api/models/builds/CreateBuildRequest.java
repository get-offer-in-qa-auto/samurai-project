package api.models.builds;

import api.models.BaseModel;
import api.models.buildConfiguration.BuildType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBuildRequest extends BaseModel {
    private BuildType buildType;

}
