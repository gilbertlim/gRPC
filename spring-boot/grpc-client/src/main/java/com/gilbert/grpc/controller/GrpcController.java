package com.gilbert.grpc.controller;

import com.gilbert.grpc.service.GrpcClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GrpcController {

    private final GrpcClientService grpcClientService;

    @GetMapping("/sampleService")
    public void sample() {
        log.info("GrpcController#sample");
        grpcClientService.sampleCall();
    }
}
