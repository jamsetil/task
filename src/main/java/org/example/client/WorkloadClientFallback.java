package org.example.client;

import lombok.extern.slf4j.Slf4j;
import org.example.config.TransactionLoggingInterceptor;
import org.example.dto.request.TrainerWorkloadRequest;
import org.slf4j.MDC;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorkloadClientFallback implements FallbackFactory<WorkloadClient> {

    @Override
    public WorkloadClient create(Throwable cause) {
        return new WorkloadClient() {
            @Override
            public void updateTrainerWorkload(String authorizationHeader, TrainerWorkloadRequest request) {
                logUnavailable(request, cause);
            }

            @Override
            public void deleteTrainerWorkload(String authorizationHeader, TrainerWorkloadRequest request) {
                logUnavailable(request, cause);
            }
        };
    }

    private static void logUnavailable(TrainerWorkloadRequest request, Throwable cause) {
        log.warn("[transactionId={}] Workload service unavailable for trainer={} action={}: {}",
                MDC.get(TransactionLoggingInterceptor.TRANSACTION_ID),
                request.getTrainerUsername(),
                request.getActionType(),
                cause.getMessage());
    }
}
