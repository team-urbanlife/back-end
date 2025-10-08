package com.wegotoo.infra.mq.scheduler;

import com.wegotoo.infra.mq.RabbitProducer;
import com.wegotoo.infra.mq.domain.store.ReturnedMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReturnedMessageScheduler {

    private final RabbitProducer rabbitProducer;
    private final ReturnedMessages returnedMessages;
    private static final int BATCH_SIZE = 100;

    @Scheduled(fixedDelay = 5000)
    public void scheduleReturnedMessages() {
        for (int i = 0; i < BATCH_SIZE; i++) {
            if (returnedMessages.isEmpty()) {
                break;
            }

            Message message = returnedMessages.remove();
            rabbitProducer.send(message);
        }
    }

}
