package com.wegotoo.infra.mq.domain.store;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
public class ReturnedMessages {

    private final Queue<Message> returnedMessages = new ConcurrentLinkedQueue<>();

    public void add(Message message) {
        returnedMessages.add(message);
    }

    public Message remove() {
        return returnedMessages.poll();
    }

    public boolean isEmpty() {
        return returnedMessages.isEmpty();
    }

}
