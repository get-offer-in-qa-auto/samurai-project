package api.models.buildConfiguration;
import lombok.Data;

@Data
public class Project {
    private String id;
    private String name;
    private String parentProjectId;
    private String description;
    private String href;
    private String webUrl;
}
