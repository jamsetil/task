package org.example.client;

import org.example.dto.request.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "trainer-workload-service",
        fallbackFactory = WorkloadClientFallback.class,
        configuration = WorkloadFeignClientConfig.class
)
public interface WorkloadClient {

    @PostMapping("/api/v1/trainer-workloads")
    void updateTrainerWorkload(
            @RequestHeader ("Authorization") String authorizationHeader,
            @RequestBody TrainerWorkloadRequest request);

    @DeleteMapping("/api/v1/trainer-workloads")
    void deleteTrainerWorkload(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody TrainerWorkloadRequest request);
}
