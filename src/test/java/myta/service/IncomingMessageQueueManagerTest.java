package myta.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Date;

import org.junit.jupiter.api.Test;

import myta.message.model.Message;
import myta.queue.model.IncomingMessageQueueEntry;

class IncomingMessageQueueManagerTest {

    @Test
    public void testEnqueueMesssage() throws IOException, InterruptedException {

        IncomingMessageQueueManager queueManager = new IncomingMessageQueueManager(10);

        // queue must be empty
        assertEquals(0, queueManager.getQueueSize());

        Message message = new Message();
        message.setSubject("TEST SUBJECT");

        Date now = new Date();

        IncomingMessageQueueEntry entry = new IncomingMessageQueueEntry(now, message);

        queueManager.enqueueMessage(entry);

        // queue must now hold 1 entry
        assertEquals(1, queueManager.getQueueSize());

        IncomingMessageQueueEntry entryFromQueue = queueManager.takeQueueEntry();

        assertNotNull(entryFromQueue);
        assertNotNull(entryFromQueue.getReceivedAt());
        assertNotNull(entryFromQueue.getMessage());

        assertEquals("TEST SUBJECT", entry.getMessage().getSubject());
        assertEquals(now.getTime(), entry.getReceivedAt().getTime());

        // queue must be empty now again
        assertEquals(0, queueManager.getQueueSize());

    }

}
