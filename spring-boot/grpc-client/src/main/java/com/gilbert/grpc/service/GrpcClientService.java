package com.gilbert.grpc.service;

import com.gilbert.grpc.proto.SampleRequest;
import com.gilbert.grpc.proto.SampleResponse;
import com.gilbert.grpc.proto.SampleServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GrpcClientService {

    @GrpcClient("local-grpc-server")
    private  SampleServiceGrpc.SampleServiceBlockingStub sampleStub;

    public void sampleCall() {
        SampleRequest sampleRequest = SampleRequest.newBuilder()
            .setUserId("gilbert")
            .setMessage("grpc request")
            .build();

        try {
            log.info("GrpcClientService#sampleStub.sampleCall - {}", sampleRequest);
            SampleResponse sampleResponse = sampleStub.sampleCall(sampleRequest);
            log.info("GrpcClientService#sampleResponse - {}", sampleResponse);
        } catch (StatusRuntimeException e) {
            log.error("RPC failed: {}", e.getStatus());
        }
    }
}
