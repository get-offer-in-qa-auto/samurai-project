package api.models.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VcsRoot {
    public String name;
    public String vcsName;
    public List<Property> properties = List.of(
            new Property("url", "https://github.com/filangelin/NBank-tests"),
            new Property("branch", "refs/heads/main"),
            new Property("authMethod", "ANONYMOUS"));
}