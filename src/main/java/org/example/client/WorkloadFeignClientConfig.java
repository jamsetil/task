package org.example.client;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class WorkloadFeignClientConfig {

    @Bean
    RequestInterceptor workloadFeignRequestInterceptor() {
        return new WorkloadFeignRequestInterceptor();
    }
}
