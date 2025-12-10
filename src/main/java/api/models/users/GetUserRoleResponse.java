package api.models.users;

import api.models.BaseModel;
import lombok.*;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUserRoleResponse extends BaseModel {
    private List<CreateUserRoleResponse> role;
}