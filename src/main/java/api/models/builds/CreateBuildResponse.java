package api.models.builds;
import api.models.BaseModel;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.models.buildConfiguration.BuildType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CreateBuildResponse extends BaseModel {
    private Integer id;
    private String state;
    private String href;
    private String webUrl;
    private BuildType buildType;

}
