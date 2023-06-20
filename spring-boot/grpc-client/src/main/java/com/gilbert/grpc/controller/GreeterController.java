package com.gilbert.grpc.controller;

import com.gilbert.grpc.config.GrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GreeterController {

    private final GrpcClient grpcClient;

    @GetMapping("/")
    public String test() {
        return grpcClient.sampleCall();
    }
}
