package common.extensions;

import api.models.agent.AgentPool;
import common.annotations.TestAgent;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class AgentExtension implements BeforeEachCallback, AfterEachCallback {
    private static final AgentPool POOL = new AgentPool();

    // Храним агента, выделенного для текущего теста (на 1 поток)
    private static final ThreadLocal<api.models.agent.Agent> CURRENT_AGENT = new ThreadLocal<>();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        TestAgent annotation = extensionContext.getRequiredTestMethod().getAnnotation(TestAgent.class);
        if (annotation != null) {

            boolean needAuthorized = annotation.authorized();
            boolean needEnabled = annotation.enabled();

            api.models.agent.Agent agent = POOL.acquire(needAuthorized, needEnabled);
            CURRENT_AGENT.set(agent);
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        TestAgent annotation = extensionContext.getRequiredTestMethod().getAnnotation(TestAgent.class);
        if (annotation != null) {
            api.models.agent.Agent agent = CURRENT_AGENT.get();
            if (agent != null) {
                POOL.release(agent);
                CURRENT_AGENT.remove();
            }
        }
    }

    // Утилита: получить агента в тесте
    public static api.models.agent.Agent getCurrentAgent() {
        api.models.agent.Agent agent = CURRENT_AGENT.get();
        if (agent == null) {
            throw new IllegalStateException(
                    "No agents. Add @TestAgent to your test method.");
        }
        return agent;
    }
}
