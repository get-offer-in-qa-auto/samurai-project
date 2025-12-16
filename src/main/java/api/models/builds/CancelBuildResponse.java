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
public class CancelBuildResponse extends BaseModel {
    private Integer id;
    private String state;
    private String statusText;
    private String href;
    private String webUrl;

    private BuildType buildType;
    private CanceledInfo canceledInfo;

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CanceledInfo {
        private String timestamp;
        private String text;
        private User user;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class User {
        private String username;
        private Integer id;
        private String href;
    }
}
