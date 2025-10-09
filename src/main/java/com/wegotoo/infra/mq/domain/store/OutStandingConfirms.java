package com.wegotoo.infra.mq.domain.store;

import com.wegotoo.infra.mq.domain.PublishMessage;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class OutStandingConfirms {

    private final ConcurrentHashMap<String, PublishMessage> outstandingConfirms = new ConcurrentHashMap<>();

    public void add(String correlationId, PublishMessage message) {
        outstandingConfirms.put(correlationId, message);
    }

    public PublishMessage remove(String correlationId) {
        return outstandingConfirms.remove(correlationId);
    }

}
