package api.models.project;

import api.models.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetProjectsResponse extends BaseModel {
    public int count;
    public String href;
    public List<Project> project;

    @Data
    public static class Project {
        public String id;
        public String name;
        public String parentProjectId;
        public String description;
        public String href;
        public String webUrl;
    }
}