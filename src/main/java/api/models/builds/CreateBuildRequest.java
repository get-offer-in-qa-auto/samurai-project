package api.models.builds;
import api.models.BaseModel;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
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
