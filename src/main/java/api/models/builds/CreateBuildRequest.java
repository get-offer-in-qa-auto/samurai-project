package api.models.builds;
import api.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import api.models.buildConfiguration.BuildType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CreateBuildRequest extends BaseModel {
    private BuildType buildType;

}
