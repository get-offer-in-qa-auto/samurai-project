package api.models.builds;

import api.models.BaseModel;
import api.models.buildconfiguration.BuildType;
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
public class GetBuildResponse extends BaseModel {
    private Integer id;
    private String buildTypeId;
    private String state;
    private String href;
    private String webUrl;
    private BuildType buildType;
}
