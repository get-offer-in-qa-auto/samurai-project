package ui.pages.agents;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        agentsPageTitle.shouldHave(text("Agents"));
        return this;
    }

    public AgentsSidebar expandListOfAgents() {
        agentsExpandButton.shouldBe(visible).click();
        return this;
    }

    public AgentsSidebar checkStatusAgentByName(String agentName, String expectedStatus) {
        Map<String, String> agentNames = getAgentNameAndStatusMap();
        assertEquals(agentNames.get(agentName), expectedStatus.toLowerCase());
        return this;
    }

    public AgentsSidebar findAgentInPoolsByName(String agentName) {

        String buttonTitle = agentPoolsExpandButton.getAttribute("title");
        if (!"Collapse".equals(buttonTitle)) {
            agentPoolsExpandButton.shouldBe(visible).click();
        }
        agentElements.findBy(text(agentName)).shouldBe(visible);
        return this;
    }

    public AgentsPage clickAgentByName(String agentName) {
        AgentsPage agentsPage = new AgentsPage();
        agentElements.findBy(Condition.text(agentName)).shouldBe(visible).click();
        return agentsPage;
    }

    public AgentsPage clickAgentInPoolsByName(String agentName) {
        AgentsPage agentsPage = new AgentsPage();
        agentPoolsExpandButton.shouldBe(visible).click();
        agentElements.findBy(Condition.text(agentName)).shouldBe(visible).click();
        return agentsPage;
    }

    public Map<String, String> getAgentNameAndStatusMap() {
        return agentElements.stream()
                .map(SelenideElement::getText)
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toMap(
                        text -> text.split("\n")[0],     // ключ: имя агента
                        text -> text.split("\n").length > 1 ? text.split("\n")[1] : ""
                ));
    }
}
