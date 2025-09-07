package com.wegotoo.application.notification.event;

import com.wegotoo.domain.notification.Notification;
import com.wegotoo.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationEvent {

    private Long fromId;
    private Long toId;
    private String fromName;
    private String toName;
    private String event;
    private String message;

    @Builder
    private NotificationEvent(Long fromId, Long toId, String fromName, String toName, String event, String message) {
        this.fromId = fromId;
        this.toId = toId;
        this.fromName = fromName;
        this.toName = toName;
        this.event = event;
        this.message = message;
    }

    public Notification toEntity(User from, User to) {
        return Notification.builder()
                .user(to)
                .sender(from)
                .event(event)
                .message(message)
                .build();
    }

    public static NotificationEvent of(User from, User to, String event) {
        return NotificationEvent.builder()
                .fromId(from.getId())
                .toId(to.getId())
                .fromName(from.getName())
                .toName(from.getName())
                .event(event)
                .message(createMessage(from, event))
                .build();
    }

    private static String createMessage(User from, String event) {
        return from.getName() + "님이 " + event + " 메세지를 전송했습니다.";
    }

}
