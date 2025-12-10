package api.models.buildConfiguration;

import lombok.Data;
import java.util.List;

@Data
public class Settings {
    private int count;
    private List<Property> property;
}
