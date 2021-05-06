package myta.queue.service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import myta.queue.model.IncomingMessageQueueEntry;

public class IncomingMessageQueueManager {

    private final BlockingQueue<IncomingMessageQueueEntry> queue;

    private final int                                      queueMaxSize;

    public IncomingMessageQueueManager(int queueMaxSize) {

        this.queue = new ArrayBlockingQueue<IncomingMessageQueueEntry>(queueMaxSize, true);
        this.queueMaxSize = queueMaxSize;

    }

    public void enqueueMessage(IncomingMessageQueueEntry entry) {

        try {

            this.queue.put(entry);

        } catch (InterruptedException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();

        }

    }

    public IncomingMessageQueueEntry takeQueueEntry() throws InterruptedException {

        return queue.take();

    }

    public int getQueueSize() {

        return this.queue.size();

    }

    public int getQueueMaxSize() {

        return this.queueMaxSize;

    }

}
