package api.models.buildConfiguration;

import api.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor

public class GetAllBuildTypeResponse extends BaseModel {
    public int count;
    public String href;
    public List<BuildType> buildTypes;
}
