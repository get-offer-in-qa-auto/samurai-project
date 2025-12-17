package api.models.builds;

import api.models.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildQueueItem extends BaseModel {
    private Integer id;
    private String state;
    private String buildTypeId;
    private String href;
    private String webUrl;
}
