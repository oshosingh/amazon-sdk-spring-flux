package com.poc.awspoc.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.poc.awspoc.config.SnsConfig;
import io.awspring.cloud.messaging.core.NotificationMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SnsController {

    @Value("${subscribe.endpoint.url}")
    private String endpointUrl;

    @Autowired
    private NotificationMessagingTemplate snsTemplate;

    @Autowired
    private SnsConfig snsConfig;

    @PostMapping("/publish/sns")
    String publishMessageToSnsTopic() {
        String message = "hello world 1";
        snsTemplate.convertAndSend(message);
        return "sent";
    }

    @PostMapping("/sns/listener")
    ResponseEntity<Object> listenSNSMessages(@RequestHeader("x-amz-sns-message-type") String snsMessageType,
                                             @RequestBody JsonNode snsMessage) {
        return null;
    }

}
