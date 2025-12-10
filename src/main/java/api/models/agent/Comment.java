package api.models.agent;

import api.models.BaseModel;
import api.models.users.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Comment extends BaseModel {
    private String timestamp;
    private String text;
    private User user;
}
