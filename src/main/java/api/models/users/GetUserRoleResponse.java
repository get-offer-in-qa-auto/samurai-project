package api.models.users;

import api.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUserRoleResponse extends BaseModel {
    private List<CreateUserRoleResponse> role;
}
