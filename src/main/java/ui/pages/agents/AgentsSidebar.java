package ui.pages.agents;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import common.helpers.StepLogger;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Getter
public class AgentsSidebar {
    private final SelenideElement agentsSidebarModule = $("[class*='AgentsSidebar-module']");
    private final SelenideElement agentsPageTitle = agentsSidebarModule.$("[class*='SidebarHeaderShadow-module']");
    private final SelenideElement agentsCollapseButton = agentsSidebarModule.$("[data-test='collapse-button']");
    private final SelenideElement agentsExpandButton = agentsSidebarModule.$("[data-test='expand-button']");
    private final SelenideElement container = $("[data-test='overview']")
            .closest(".ReactVirtualized__Grid__innerScrollContainer");
    private final ElementsCollection agentElements = container.$$("[data-test='agent']");
    private final SelenideElement agentPoolsExpandButton = $("[data-test='agent-pool']").$("button");

    public AgentsSidebar checkAgentsSideBarTitle() {
        return StepLogger.log("Проверка заголовка боковой панели 'Agents'", () -> {
            agentsPageTitle.shouldHave(text("Agents"));
            return this;
        });
    }

    public AgentsSidebar expandListOfAgents() {
        return StepLogger.log("Развернуть список агентов в боковой панели", () -> {
            agentsExpandButton.shouldBe(visible).click();
            return this;
        });
    }

    public AgentsSidebar checkStatusAgentByName(String agentName, String expectedStatus) {
        return StepLogger.log("Проверка, что  статус агента '" + agentName + "' соотвествует ожидаемому: " + expectedStatus, () -> {
            Map<String, String> agentNames = getAgentNameAndStatusMap();
            assertEquals(agentNames.get(agentName), expectedStatus.toLowerCase());
            return this;
        });
    }

    public AgentsSidebar findAgentInPoolsByName(String agentName) {
        String buttonTitle = agentPoolsExpandButton.getAttribute("title");
        return StepLogger.log("Поиск агента '" + agentName + "' в пуле агентов", () -> {
            if (!"Collapse".equals(buttonTitle)) {
                agentPoolsExpandButton.shouldBe(visible).click();
            }
            agentElements.findBy(text(agentName)).shouldBe(visible);
            return this;
        });
    }

    public AgentsPage clickAgentByName(String agentName) {
        return StepLogger.log("Клик по агенту по имени'" + agentName + "' в основном списке", () -> {
            AgentsPage agentsPage = new AgentsPage();
            agentElements.findBy(Condition.text(agentName)).shouldBe(visible).click();
            return agentsPage;
        });
    }

    public AgentsPage clickAgentInPoolsByName(String agentName) {
        return StepLogger.log("Клик по агенту по имени '" + agentName + "' в списке авторизованныз агентов", () -> {
            AgentsPage agentsPage = new AgentsPage();
            agentPoolsExpandButton.shouldBe(visible).click();
            agentElements.findBy(Condition.text(agentName)).shouldBe(visible).click();
            return agentsPage;
        });
    }

    public Map<String, String> getAgentNameAndStatusMap() {
        return StepLogger.log("Получение списка имен и статусов всех агентов", () ->
                agentElements.stream()
                        .map(SelenideElement::getText)
                        .filter(text -> !text.isEmpty())
                        .collect(Collectors.toMap(
                                text -> text.split("\n")[0],     // ключ: имя агента
                                text -> text.split("\n").length > 1 ? text.split("\n")[1] : ""
                        ))
        );
    }
}
