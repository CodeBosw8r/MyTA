package myta.queue.model;

import java.util.Date;

import myta.message.model.Message;

public class IncomingMessageQueueEntry {

    private final Date    receivedAt;

    private final Message message;

    public IncomingMessageQueueEntry(Date receivedAt, Message message) {
        super();
        this.receivedAt = receivedAt;
        this.message = message;
    }

    public Date getReceivedAt() {
        return receivedAt;
    }

    public Message getMessage() {
        return message;
    }

}
