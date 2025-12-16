package api.models.builds;
import api.models.BaseModel;
import lombok.*;

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BuildType {
        private String id;
        private String name;
        private String projectId;
        private String projectName;
    }
}
