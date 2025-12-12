package api.models.agent;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static api.requests.steps.UserSteps.getAllAgents;
import static api.requests.steps.UserSteps.setAgentAuthorizedStatus;
import static api.requests.steps.UserSteps.setAgentEnabledStatus;


public class AgentPool {

    private volatile boolean initialized = false;
    private final Queue<Agent> availableAgents = new ConcurrentLinkedQueue<>();


    private synchronized void initializeIfNeeded() {
        if (initialized) return;

        // Получаем всех агентов из TeamCity
        List<Agent> allAgents = getAllAgents();

        if (allAgents.isEmpty()) {
            throw new IllegalStateException("No agents found in TeamCity!");
        }

        availableAgents.addAll(allAgents);
        initialized = true;
    }

    public synchronized Agent acquire(boolean targetAuthorized, boolean targetEnable) {
        initializeIfNeeded();
        Agent agent = availableAgents.poll();

        if (agent == null) {
            throw new IllegalStateException("No free test agents available!");
        }
        //Приводим агент в нужное состояние
        if (targetAuthorized) {
            setAgentAuthorizedStatus(true, agent.getId());
        }
        if (targetEnable) {
            setAgentEnabledStatus(true, agent.getId());
        }

        return agent;
    }

    public synchronized void release(Agent agent) {
        if (agent != null) {
            // Возвращаем агента в "чистое" состояние:
            setAgentAuthorizedStatus(false, agent.getId());        // 1. Деавторизуем
            setAgentEnabledStatus(false, agent.getId());           // 2. Отключаем

            availableAgents.offer(agent);
        }
    }
}

