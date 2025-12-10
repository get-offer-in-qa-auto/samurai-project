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
public class GetBuildResponse extends BaseModel {
    private Integer id;
    private String buildTypeId;
    private String state;
    private String href;
    private String webUrl;
}
