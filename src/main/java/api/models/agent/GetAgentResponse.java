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
public class GetAgentResponse extends BaseModel {
    private Integer id;
    private String name;
    private Integer typeId;
    private String href;
    private String webUrl;
}
