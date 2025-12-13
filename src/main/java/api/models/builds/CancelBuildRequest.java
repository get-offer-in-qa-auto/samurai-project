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

public class CancelBuildRequest extends BaseModel {
    private String comment;
    private Boolean readdIntoQueue;
}
