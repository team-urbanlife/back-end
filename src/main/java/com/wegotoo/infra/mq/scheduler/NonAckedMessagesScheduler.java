package com.wegotoo.infra.mq.scheduler;

import com.wegotoo.infra.mq.RabbitProducer;
import com.wegotoo.infra.mq.domain.NonAckedMessage;
import com.wegotoo.infra.mq.domain.PublishMessage;
import com.wegotoo.infra.mq.domain.store.NonAckedMessages;
import com.wegotoo.infra.mq.domain.store.OutStandingConfirms;
import com.wegotoo.infra.slack.SlackProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NonAckedMessagesScheduler {

    private final RabbitProducer rabbitProducer;
    private final OutStandingConfirms outStandingConfirms;
    private final NonAckedMessages nonAckedMessages;
    private final SlackProducer slackProducer;
    private static final int BATCH_SIZE = 100;

    @Scheduled(fixedDelay = 5000)
    public void scheduleNonAckedMessages() {
        for (int i = 0; i < BATCH_SIZE; i++) {
            if (nonAckedMessages.isEmpty()) {
                break;
            }

            NonAckedMessage nonAckedMessage = nonAckedMessages.remove();
            tryRepublish(nonAckedMessage.getId(), nonAckedMessage.getRetryCount());
        }
    }

    private void tryRepublish(String correlationId, int retryCount) {
        PublishMessage publishMessage = outStandingConfirms.remove(correlationId);

        if (publishMessage == null) {
            return;
        }

        if (retryCount >= 3) {
            slackProducer.sendMessage(publishMessage);
            return;
        }

        rabbitProducer.send(publishMessage, correlationId, retryCount + 1);
    }

}
