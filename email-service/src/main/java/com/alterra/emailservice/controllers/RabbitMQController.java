package com.alterra.emailservice.controllers;

import com.alterra.emailservice.dtos.EmailRequest;
import com.alterra.emailservice.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableRabbit
@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class RabbitMQController {

    private final EmailService emailService;

    @RabbitListener(queues = {"email"})
    public void receive(@Payload EmailRequest payload) {
        emailService.sendSimpleMail(payload.getTo(), payload.getSubject(), payload.getBodyText());
    }
}
