package myta.service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import myta.queue.model.IncomingMessageQueueEntry;

public class IncomingMessageQueueManager {

    private final BlockingQueue<IncomingMessageQueueEntry> queue;

    public IncomingMessageQueueManager(int queueSize) {

        this.queue = new ArrayBlockingQueue<IncomingMessageQueueEntry>(queueSize, true);

    }

    public void enqueueMessage(IncomingMessageQueueEntry entry) {

        this.queue.add(entry);

    }

    public IncomingMessageQueueEntry takeQueueEntry() throws InterruptedException {

        return queue.take();

    }

    public int getQueueSize() {

        return this.queue.size();

    }

}
