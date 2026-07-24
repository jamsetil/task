package org.example.messaging;

import org.example.config.TransactionLoggingInterceptor;
import org.example.dto.request.TrainerWorkloadRequest;
import org.example.enums.ActionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.jms.Message;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadMessagePublisherTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private WorkloadMessagePublisher publisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(publisher, "trainerWorkloadQueue", "trainer.workload.queue");
    }

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void publish_sendsMessageWithTransactionId() throws Exception {
        MDC.put(TransactionLoggingInterceptor.TRANSACTION_ID, "tx-123");
        var request = TrainerWorkloadRequest.builder()
                .trainerUsername("trainer1")
                .trainerFirstName("Ann")
                .trainerLastName("Lee")
                .isActive(true)
                .trainingDate(LocalDate.of(2024, 6, 1))
                .trainingDuration(60)
                .actionType(ActionType.ADD)
                .build();

        publisher.publish(request);

        ArgumentCaptor<MessagePostProcessor> postProcessorCaptor =
                ArgumentCaptor.forClass(MessagePostProcessor.class);
        verify(jmsTemplate).convertAndSend(
                eq("trainer.workload.queue"), eq(request), postProcessorCaptor.capture());

        Message message = mock(Message.class);
        postProcessorCaptor.getValue().postProcessMessage(message);
        verify(message).setStringProperty(TransactionLoggingInterceptor.TRANSACTION_ID, "tx-123");
    }
}
