package com.wegotoo.infra.mq.domain;

import com.wegotoo.application.notification.event.NotificationEvent;
import com.wegotoo.domain.notification.Notification;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationPublishMessage implements PublishMessage {

    private Long id;
    private Long fromId;
    private Long toId;
    private String fromName;
    private String toName;
    private String event;
    private String message;

    @Builder
    private NotificationPublishMessage(Long id, Long fromId, Long toId, String fromName, String toName, String event,
                                      String message) {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.fromName = fromName;
        this.toName = toName;
        this.event = event;
        this.message = message;
    }

    public NotificationPublishMessage of(Notification notification, NotificationEvent event) {
        return NotificationPublishMessage.builder()
                .id(notification.getId())
                .fromId(event.getFromId())
                .toId(event.getToId())
                .fromName(event.getFromName())
                .toName(event.getToName())
                .event(event.getEvent())
                .message(event.getMessage())
                .build();
    }

    @Override
    public String getMessageId() {
        return String.format("notification:%s", id);
    }

}
