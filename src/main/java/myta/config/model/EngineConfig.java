package myta.config.model;

import java.util.ArrayList;
import java.util.List;

public class EngineConfig {

    private int                     numWorkers;

    private int                     incomingMessageQueueSize;

    private List<SmtpConfiguration> relayServers;

    public EngineConfig() {

        this.numWorkers = 2;
        this.incomingMessageQueueSize = 1000;

        SmtpConfiguration relayServer = new SmtpConfiguration("localhost");
        this.relayServers = new ArrayList<SmtpConfiguration>(1);
        this.relayServers.add(relayServer);

    }

    public int getNumWorkers() {
        return numWorkers;
    }

    public void setNumWorkers(int numWorkers) {
        this.numWorkers = numWorkers;
    }

    public int getIncomingMessageQueueSize() {
        return incomingMessageQueueSize;
    }

    public void setIncomingMessageQueueSize(int incomingMessageQueueSize) {
        this.incomingMessageQueueSize = incomingMessageQueueSize;
    }

    public List<SmtpConfiguration> getRelayServers() {
        return relayServers;
    }

    public void setRelayServers(List<SmtpConfiguration> relayServers) {
        this.relayServers = relayServers;
    }

}
