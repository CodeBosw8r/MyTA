package myta.queue.service;

import myta.core.Engine;
import myta.queue.model.IncomingMessageQueueEntry;

public class IncomingMessageQueueProcessingThread extends Thread {

    private final Engine                      engine;

    private final IncomingMessageQueueManager incomingMessageQueueManager;

    private boolean                           paused;

    public IncomingMessageQueueProcessingThread(Engine engine, String name) {

        super(name);
        this.engine = engine;
        this.incomingMessageQueueManager = engine.getIncomingMessageQueueManager();
        this.paused = false;

    }

    public boolean isPaused() {
        return this.paused;
    }

    @Override
    public void run() {

        try {

            while (true) {

                if (!this.paused) {

                    IncomingMessageQueueEntry entry = this.incomingMessageQueueManager.takeQueueEntry();

                    if (entry != null) {

                        this.engine.processIncomingMessage(entry.getMessage());

                    }
                }

                if (this.paused) {
                    // sleep for a second and try again
                    Thread.sleep(1000);
                }

            }

        } catch (InterruptedException e) {
            return;
        }

    }

    public void pause() {
        this.paused = true;
    }

    public void unpause() {
        this.paused = false;
    }

}
