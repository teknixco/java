package com.example.demo.service.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Service
public class JmsService {

    @Value("${jms.sqs.queue.name}")
    private String queueName;

    @Value("${jms.sqs.dlq.name}")
    private String dlqName;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private RetryTemplate retryTemplate;

    private ConcurrentLinkedQueue<String> internalQueue = new ConcurrentLinkedQueue<>();

    public void sendMessage(String message) {
        jmsTemplate.convertAndSend(queueName, message);
    }

    public void sendToDlq(String message) {
        jmsTemplate.convertAndSend(dlqName, message);
    }

    @JmsListener(destination = "${jms.sqs.queue.name}")
    public void receiveMessage(String message, Session session) throws JMSException {
        try {
            retryTemplate.execute(context -> {
                processMessage(message);
                return null;
            });
        } catch (Exception e) {
            System.out.println("Retry attempts exhausted. Sending message to DLQ: " + message + " errorMessage: " + e.getMessage() + " errorCause: " + e.getCause());
            sendToDlq(message);
        }
    }

    public void processMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.println("Received message from " + queueName + ": " + message + " on thread: " + threadName);
        throw new RuntimeException("exception thrown test");
    }

    public List<String> getReceivedMessages() {
        return internalQueue.stream().collect(Collectors.toList());
    }
}