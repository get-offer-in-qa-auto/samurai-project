package api.models.builds;

import api.models.BaseModel;
import api.models.users.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import api.models.buildconfiguration.BuildType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
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
    public static class CanceledInfo {
        private String timestamp;
        private String text;
        private User user;
    }
}
