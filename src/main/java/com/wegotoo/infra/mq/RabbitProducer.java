package com.wegotoo.infra.mq;

import com.wegotoo.infra.mq.domain.NonAckedMessage;
import com.wegotoo.infra.mq.domain.PublishMessage;
import com.wegotoo.infra.mq.domain.store.NonAckedMessages;
import com.wegotoo.infra.mq.domain.store.OutStandingConfirms;
import com.wegotoo.infra.mq.domain.store.ReturnedMessages;
import com.wegotoo.infra.slack.SlackProducer;
import jakarta.annotation.PostConstruct;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitProducer implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    private final RabbitTemplate rabbitTemplate;
    private final MessageHeaderAccessor messageHeaderAccessor;
    private final OutStandingConfirms outStandingConfirms;
    private final NonAckedMessages nonAckedMessages;
    private final ReturnedMessages returnedMessages;
    private final SlackProducer slackProducer;

    private static final int PUBLISHER_MAX_RETRIES = 3;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    public void send(PublishMessage message) {
        String messageId = message.getMessageId();
        String correlationId = UUID.randomUUID().toString();

        outStandingConfirms.add(correlationId, message);
        rabbitTemplate.convertAndSend(message, m -> {
            m.getMessageProperties().setMessageId(messageId);
            m.getMessageProperties().setCorrelationId(correlationId);
            return m;
        }, new CorrelationData(correlationId));
    }

    public void send(PublishMessage message, String correlationId, int retryCount) {
        outStandingConfirms.add(correlationId, message);
        rabbitTemplate.convertAndSend(message, m -> {
            messageHeaderAccessor.addCorrelationId(m);
            messageHeaderAccessor.addPublishRetryCountHeader(m, retryCount);
            return m;
        }, NonAckedMessage.of(correlationId, retryCount));
    }

    public void send(Message message) {
        PublishMessage publishMessage = (PublishMessage) rabbitTemplate.getMessageConverter().fromMessage(message);
        String correlationId = messageHeaderAccessor.getReturnedCorrelationId(message);
        int retryCount = messageHeaderAccessor.getPublishRetryCount(message);

        outStandingConfirms.add(correlationId, publishMessage);
        rabbitTemplate.correlationConvertAndSend(message, NonAckedMessage.of(correlationId, retryCount));
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            outStandingConfirms.remove(correlationData.getId());
        } else {
            if (correlationData instanceof NonAckedMessage nonAckedMessage) {
                nonAckedMessages.add(correlationData.getId(), nonAckedMessage.getRetryCount());
            } else {
                nonAckedMessages.add(correlationData.getId(), 0);
            }
        }
    }

    @Override
    public void returnedMessage(ReturnedMessage returned) {
        Message message = returned.getMessage();
        int publisherRetryCount = messageHeaderAccessor.getPublishRetryCount(message);

        if (publisherRetryCount >= PUBLISHER_MAX_RETRIES) {
            slackProducer.sendMessage(message);
            return;
        }

        messageHeaderAccessor.addCorrelationId(message);
        messageHeaderAccessor.addPublishRetryCountHeader(message, publisherRetryCount + 1);

        returnedMessages.add(message);
    }

}
