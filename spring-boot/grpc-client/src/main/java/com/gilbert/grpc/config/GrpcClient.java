package com.gilbert.grpc.config;

import com.gilbert.grpc.proto.SampleRequest;
import com.gilbert.grpc.proto.SampleResponse;
import com.gilbert.grpc.proto.SampleServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GrpcClient {
    private static final int PORT = 9090;
    public static final String HOST = "localhost";
    private final SampleServiceGrpc.SampleServiceStub asyncStub = SampleServiceGrpc.newStub(
        ManagedChannelBuilder.forAddress(HOST, PORT)
            .usePlaintext()
            .build()
    );

    public String sampleCall() {
        final SampleRequest sampleRequest = SampleRequest.newBuilder()
            .setUserId("levi.yoon")
            .setMessage("grpc request")
            .build();

        asyncStub.sampleCall(sampleRequest, new StreamObserver<>() {
            @Override
            public void onNext(SampleResponse value) {
                log.info("GrpcClient#sampleCall - {}", value);
            }

            @Override
            public void onError(Throwable t) {
                log.error("GrpcClient#sampleCall - onError");
            }

            @Override
            public void onCompleted() {
                log.info("GrpcClient#sampleCall - onCompleted");
            }
        });
        return "string";
    }
}
