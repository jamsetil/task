package org.example.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.example.config.TransactionLoggingInterceptor;
import org.slf4j.MDC;

@Slf4j
public class WorkloadFeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String transactionId = MDC.get(TransactionLoggingInterceptor.TRANSACTION_ID);
        if (transactionId != null) {
            template.header(TransactionLoggingInterceptor.TRANSACTION_ID, transactionId);
        }

        log.info("[transactionId={}] Outbound workload request {} {}",
                transactionId, template.method(), template.url());
    }
}
