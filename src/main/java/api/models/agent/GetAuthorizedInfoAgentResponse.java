package api.models.agent;

import api.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetAuthorizedInfoAgentResponse extends BaseModel {
    private Boolean status;
    private Comment comment;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Comment {
        private String timestamp;
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
