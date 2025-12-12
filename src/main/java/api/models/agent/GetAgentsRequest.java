package api.models.agent;

import api.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GetAgentsRequest extends BaseModel {
    public static final String AGENT_AUTHORIZATION = "Авторизация агента с id: ";
    public static final String AGENT_DEAUTHORIZATION = "Деавторизация агента с id: ";
    public static final String AGENT_ENABLING = "Включение агента с id: ";
    public static final String AGENT_DISABLING = "Выключение агента с id: ";
}
