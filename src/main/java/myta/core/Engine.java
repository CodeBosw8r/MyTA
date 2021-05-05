package myta.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import myta.config.model.SmtpConfiguration;
import myta.message.model.Message;
import myta.queue.service.IncomingMessageQueueManager;
import myta.queue.service.IncomingMessageQueueProcessingThread;
import myta.service.IncomingMessageProcessor;

public class Engine {

    private IncomingMessageQueueManager                incomingMessageQueueManager;

    private IncomingMessageProcessor                   incomingMessageProcessor;

    private List<IncomingMessageQueueProcessingThread> incomingMessageQueueProcessingThreads;

    private SmtpConfiguration                          defaultRelayConfiguration;

    public void initialize(Map<String, String> params) {

        int queueSize = 1000;
        int numWorkers = 2;
        String relayHost = "localhost";

        if (params != null) {

            if (params.containsKey("numWorkers") && (params.get("numWorkers") != null) && params.get("numWorkers").toString().matches("[1-9]{1}[0-9]{0,}")) {

                numWorkers = Integer.valueOf(params.get("numWorkers").toString());

            }

            if (params.containsKey("queueSize") && (params.get("queueSize") != null) && params.get("queueSize").toString().matches("[1-9]{1}[0-9]{0,}")) {

                queueSize = Integer.valueOf(params.get("queueSize").toString());

            }

            if (params.containsKey("relayHost") && (params.get("relayHost") != null) && !params.get("relayHost").equals("")) {

                relayHost = params.get("relayHost");

            }

        }

        IncomingMessageQueueManager incomingMessageQueueManager = new IncomingMessageQueueManager(queueSize);
        this.incomingMessageQueueManager = incomingMessageQueueManager;

        IncomingMessageProcessor incomingMessageProcessor = new IncomingMessageProcessor(this);
        this.incomingMessageProcessor = incomingMessageProcessor;

        this.incomingMessageQueueProcessingThreads = new ArrayList<IncomingMessageQueueProcessingThread>();

        for (int i = 0; i < numWorkers; i++) {

            String name = "IncomingMessageQueueProcessingThread-" + i;
            IncomingMessageQueueProcessingThread incomingMessageQueueProcessingThread = new IncomingMessageQueueProcessingThread(this, name);

            this.incomingMessageQueueProcessingThreads.add(incomingMessageQueueProcessingThread);

            incomingMessageQueueProcessingThread.start();

        }

        this.defaultRelayConfiguration = new SmtpConfiguration();
        this.defaultRelayConfiguration.setHost(relayHost);

    }

    public void shutdown() {

        if (this.incomingMessageQueueProcessingThreads != null) {

            for (IncomingMessageQueueProcessingThread incomingMessageQueueProcessingThread : this.incomingMessageQueueProcessingThreads) {

                incomingMessageQueueProcessingThread.interrupt();

            }

        }

    }

    public IncomingMessageQueueManager getIncomingMessageQueueManager() {

        return this.incomingMessageQueueManager;

    }

    public IncomingMessageProcessor getIncomingMessageProcessor() {

        return this.incomingMessageProcessor;

    }

    public void processIncomingMessage(Message message) {

        this.incomingMessageProcessor.processIncomingMessage(message);

    }

    public SmtpConfiguration getDefaultRelayConfiguration() {

        return this.defaultRelayConfiguration;

    }

}
