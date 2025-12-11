package api.models.buildconfiguration;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder


public class BuildType {
    private String id;

    public String getId() {
        return id;
    }
}
