/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0

 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package myta.core;

import java.util.ArrayList;
import java.util.List;

import myta.config.model.EngineConfig;
import myta.config.model.SmtpConfiguration;
import myta.message.model.Message;
import myta.queue.service.IncomingMessageQueueManager;
import myta.queue.service.IncomingMessageQueueProcessingThread;
import myta.service.IncomingMessageProcessor;

public class Engine {

    private boolean                                    isInitialized;

    private boolean                                    isPaused;

    private IncomingMessageQueueManager                incomingMessageQueueManager;

    private IncomingMessageProcessor                   incomingMessageProcessor;

    private List<IncomingMessageQueueProcessingThread> incomingMessageQueueProcessingThreads;

    private List<SmtpConfiguration>                    relayServers;

    private long                                       timeInitializeFinished;

    public Engine() {

        this.isInitialized = false;

    }

    public void initialize(EngineConfig engineConfig) {

        int queueSize = engineConfig.getIncomingMessageQueueSize();
        int numWorkers = engineConfig.getNumWorkers();
        this.relayServers = engineConfig.getRelayServers();

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

        this.timeInitializeFinished = System.currentTimeMillis() / 1000;
        this.isInitialized = true;

    }

    public boolean isInitialized() {
        return this.isInitialized;
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public void pause() {
        this.isPaused = true;
    }

    public void unpause() {
        this.isPaused = false;
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

    public List<SmtpConfiguration> getRelayServers() {

        return this.relayServers;

    }

    public int getNumWorkers() {
        return this.incomingMessageQueueProcessingThreads.size();
    }

    public int getIncomingQueueSize() {
        return this.incomingMessageQueueManager.getQueueSize();
    }

    public int getNumMailsSent() {
        return this.incomingMessageProcessor.getNumMailsSent();
    }

    public int getUptime() {

        long now = System.currentTimeMillis() / 1000;

        int uptime = (int) (now - this.timeInitializeFinished);

        return uptime;

    }

}
