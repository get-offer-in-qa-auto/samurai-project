package api.models.agent;

import api.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AgentStatusUpdateRequest extends BaseModel {
    private boolean status;
    private Comment comment;
}
