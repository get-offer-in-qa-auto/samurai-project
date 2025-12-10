package api.models.builds;
import api.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBuildRequest extends BaseModel {
    private BuildType buildType;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BuildType {
        private String id;
    }

}
