package com.wegotoo.infra.mq.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.amqp.rabbit.connection.CorrelationData;

@Getter
public class NonAckedMessage extends CorrelationData {

    private int retryCount;

    @Builder
    private NonAckedMessage(String correlationId, int retryCount) {
        super(correlationId);
        this.retryCount = retryCount;
    }

    public static NonAckedMessage of(String correlationId, int retryCount) {
        return NonAckedMessage.builder()
                .correlationId(correlationId)
                .retryCount(retryCount)
                .build();
    }

}
