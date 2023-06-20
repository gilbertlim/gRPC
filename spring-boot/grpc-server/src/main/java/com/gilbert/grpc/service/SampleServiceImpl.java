package com.gilbert.grpc.service;

import com.gilbert.grpc.proto.SampleRequest;
import com.gilbert.grpc.proto.SampleResponse;
import com.gilbert.grpc.proto.SampleServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SampleServiceImpl extends SampleServiceGrpc.SampleServiceImplBase {

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