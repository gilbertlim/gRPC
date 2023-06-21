package com.gilbert.grpc.service;

import com.gilbert.grpc.proto.SampleRequest;
import com.gilbert.grpc.proto.SampleResponse;
import com.gilbert.grpc.proto.SampleServiceGrpc;
import io.grpc.stub.StreamObserver;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
//@Service
@GrpcService
public class SampleService extends SampleServiceGrpc.SampleServiceImplBase {
    
    @PostConstruct
    public void test() {
        System.out.println("log = " + log);
    }

    @Override
    public void sampleCall(SampleRequest request, StreamObserver<SampleResponse> responseObserver) {
        log.info("SampleServiceImpl#sampleCall - {}, {}", request.getUserId(), request.getMessage());
        SampleResponse sampleResponse = SampleResponse.newBuilder()
            .setMessage("grpc service response")
            .build();

        responseObserver.onNext(sampleResponse);
        responseObserver.onCompleted();
    }

}