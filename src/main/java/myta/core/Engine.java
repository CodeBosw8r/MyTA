package myta.core;

import java.util.ArrayList;
import java.util.List;

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

    public void initialize() {

        int queueSize = 1000;

        IncomingMessageQueueManager incomingMessageQueueManager = new IncomingMessageQueueManager(queueSize);
        this.incomingMessageQueueManager = incomingMessageQueueManager;

        IncomingMessageProcessor incomingMessageProcessor = new IncomingMessageProcessor(this);
        this.incomingMessageProcessor = incomingMessageProcessor;

        int numWorkers = 2;

        this.incomingMessageQueueProcessingThreads = new ArrayList<IncomingMessageQueueProcessingThread>();

        for (int i = 0; i < numWorkers; i++) {

            String name = "IncomingMessageQueueProcessingThread-" + i;
            IncomingMessageQueueProcessingThread incomingMessageQueueProcessingThread = new IncomingMessageQueueProcessingThread(this, name);

            this.incomingMessageQueueProcessingThreads.add(incomingMessageQueueProcessingThread);

            incomingMessageQueueProcessingThread.start();

        }

        this.defaultRelayConfiguration = new SmtpConfiguration();
        this.defaultRelayConfiguration.setHost("localhost");

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
