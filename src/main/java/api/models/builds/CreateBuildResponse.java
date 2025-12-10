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
public class CreateBuildResponse extends BaseModel {
    private Integer id;
    private String state;
    private String href;
    private String webUrl;
    private String buildTypeId;


}
