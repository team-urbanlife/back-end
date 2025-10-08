package com.wegotoo.infra.mq.domain.store;

import com.wegotoo.infra.mq.domain.NonAckedMessage;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.stereotype.Component;

@Component
public class NonAckedMessages {

    private final Queue<NonAckedMessage> nonAckedMessages = new ConcurrentLinkedQueue<>();

    public void add(String correlationId, int retryCount) {
        nonAckedMessages.add(NonAckedMessage.of(correlationId, retryCount));
    }

    public NonAckedMessage remove() {
        return nonAckedMessages.poll();
    }

    public boolean isEmpty() {
        return nonAckedMessages.isEmpty();
    }

}
