package org.example.client;

import feign.RequestTemplate;
import org.example.config.TransactionLoggingInterceptor;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkloadFeignRequestInterceptorTest {

    private final WorkloadFeignRequestInterceptor interceptor = new WorkloadFeignRequestInterceptor();

    @Test
    void apply_addsTransactionIdHeader() {
        MDC.put(TransactionLoggingInterceptor.TRANSACTION_ID, "tx-456");

        RequestTemplate template = new RequestTemplate();
        template.uri("/api/v1/trainer-workloads");
        interceptor.apply(template);

        assertEquals("tx-456",
                template.headers().get(TransactionLoggingInterceptor.TRANSACTION_ID).iterator().next());
    }
}
