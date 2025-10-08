package com.wegotoo.infra.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlackProducer {

    @Value("${slack.webhook.url}")
    private String webhookUrl;
    private final Slack slack = Slack.getInstance();
    private final ObjectMapper om;
    private final MessageConverter messageConverter;

    public void sendMessage(Message message) {
        Object content = messageConverter.fromMessage(message);
        sendMessage(content);
    }

    public void sendMessage(Object obj) {
        String jsonMessage = convertMessageToJson(obj);
        String slackMessage = createAlertMessage(jsonMessage);

        Payload payload = Payload.builder()
                .text(slackMessage)
                .build();

        try {
            slack.send(webhookUrl, payload);
        } catch (IOException e) {
            throw new IllegalArgumentException("Slack 알림 전송 실패");
        }
    }

    private String convertMessageToJson(Object obj) {
        try {
            return om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "메세지 JSON 변환 실패";
        }
    }

    private String createAlertMessage(String message) {
        return String.format("🚨 전송 실패 수신 🚨\n전송 처리에 실패한 메시지가 있습니다.\n\n```\n%s\n```", message);
    }

}
