package com.gilbert.grpc;

import com.gilbert.grpc.service.SampleServiceImpl;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


import io.grpc.Server;
import io.grpc.ServerBuilder;

@Component
public class GrpcServer implements ApplicationRunner {
    private static final int PORT = 9090;
    private static final Server SERVER = ServerBuilder.forPort(PORT)
        .addService(new SampleServiceImpl())
        .build();
    @Override
    public void run(ApplicationArguments args) throws Exception {
        SERVER.start();
    }

}