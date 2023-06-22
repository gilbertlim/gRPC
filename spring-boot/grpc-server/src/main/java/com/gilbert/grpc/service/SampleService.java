package com.gilbert.grpc.service;

import com.gilbert.grpc.proto.SampleRequest;
import com.gilbert.grpc.proto.SampleResponse;
import com.gilbert.grpc.proto.SampleServiceGrpc;
import io.grpc.stub.StreamObserver;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
public class SampleService extends SampleServiceGrpc.SampleServiceImplBase {

    @Override
    public void sampleCall(SampleRequest request, StreamObserver<SampleResponse> responseObserver) {
        log.info("SampleService#sampleCall - {}, {}", request.getUserId(), request.getMessage());

        SampleResponse sampleResponse = SampleResponse.newBuilder()
            .setMessage("response from grpc service")
            .build();

        responseObserver.onNext(sampleResponse);
        responseObserver.onCompleted();
        log.info("SampleService#sampleCall - completed");
    }

}