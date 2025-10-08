package com.wegotoo.infra.mq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Component;

@Component
public class MessageHeaderAccessor {
    private static final String PUBLISH_RETRY_COUNT = "publish-retry-count";
    private static final String RETURNED_CORRELATION_ID = "spring_returned_message_correlation";

    public int getPublishRetryCount(Message message) {
        MessageProperties messageProperties = message.getMessageProperties();
        Object publishRetryCount = messageProperties.getHeader(PUBLISH_RETRY_COUNT);

        if (publishRetryCount instanceof Integer) {
            return (Integer) publishRetryCount;
        }

        return 0;
    }

    public String getReturnedCorrelationId(Message message) {
        return message.getMessageProperties().getHeader(RETURNED_CORRELATION_ID);
    }

    public void addPublishRetryCountHeader(Message message, int retryCount) {
        addHeader(message, PUBLISH_RETRY_COUNT, retryCount);
    }

    public void addCorrelationId(Message message) {
        String correlationId = getReturnedCorrelationId(message);
        message.getMessageProperties().setCorrelationId(correlationId);
    }

    public void addHeader(Message message, String headerName, Object value) {
        message.getMessageProperties().setHeader(headerName, value);
    }

}
