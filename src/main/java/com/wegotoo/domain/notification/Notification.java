package com.wegotoo.domain.notification;

import com.wegotoo.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    private String event;
    private String message;

    @Builder
    private Notification(User user, User sender, String event, String message) {
        this.user = user;
        this.sender = sender;
        this.event = event;
        this.message = message;
    }

    public static Notification create(User user, User sender, String event, String message) {
        return Notification.builder()
                .user(user)
                .sender(sender)
                .event(event)
                .message(message)
                .build();
    }

}