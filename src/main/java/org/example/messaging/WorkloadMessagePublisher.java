package org.example.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.TransactionLoggingInterceptor;
import org.example.dto.request.TrainerWorkloadRequest;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadMessagePublisher {

    private final JmsTemplate jmsTemplate;

    @Value("${app.messaging.trainer-workload-queue}")
    private String trainerWorkloadQueue;

    public void publish(TrainerWorkloadRequest request) {
        String transactionId = MDC.get(TransactionLoggingInterceptor.TRANSACTION_ID);
        log.info("[transactionId={}] Publishing workload message actionType={} trainer={}",
                transactionId, request.getActionType(), request.getTrainerUsername());

        jmsTemplate.convertAndSend(trainerWorkloadQueue, request, message -> {
            if (transactionId != null) {
                message.setStringProperty(TransactionLoggingInterceptor.TRANSACTION_ID, transactionId);
            }
            return message;
        });
    }
}
