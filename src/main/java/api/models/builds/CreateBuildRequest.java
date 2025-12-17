package api.models.builds;
import api.models.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import api.models.buildconfiguration.BuildType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBuildRequest extends BaseModel {
    private BuildType buildType;

}
