# grpc-client-spring-boot-starter
https://github.com/yidongnan/grpc-spring-boot-starter

# Compatibility
https://yidongnan.github.io/grpc-spring-boot-starter/en/versions.html#version-2x

|         grpc-spring-boot-starter         |    version     | spring-boot | spring-cloud |
|:----------------------------------------:|:--------------:|:-----------:|:------------:|
|    net.devh:grpc-spring-boot-starter     | 2.14.0.RELEASE |   2.6.13    |   2021.0.5   |
| net.devh:grpc-server-spring-boot-starter | 2.14.0.RELEASE |   2.6.13    |   2021.0.5   |
| net.devh:grpc-client-spring-boot-starter | 2.14.0.RELEASE |   2.6.13    |   2021.0.5   |
# Gradle
- Spring Boot, gRPC dependency 설정
- proto compile script 작성
    - src/main/proto 경로의 `*.proto` 파일을 java가 읽을 수 있도록 compile
    - src/generated 경로에 compile 된 파일들이 생성됨
    - 추후 이 파일들을 사용할 것임

build.gradle
```java
buildscript {
	ext {
		protobufVersion = '3.19.1'
		protobufPluginVersion = '0.8.18'
		grpcVersion = '1.51.0'
	}
}

plugins {
	id 'java'
	id 'org.springframework.boot' version '2.6.13'
	id 'io.spring.dependency-management' version '1.1.0'
	id 'com.google.protobuf' version "${protobufPluginVersion}"
}

group = 'com.gilbert.grpc'
version = '0.0.1-SNAPSHOT'

java {
	sourceCompatibility = '17'
}

repositories {
	mavenCentral()
}

dependencies {
	// spring boot
	implementation 'org.springframework.boot:spring-boot-starter-web'
	compileOnly 'org.projectlombok:lombok:1.18.26'
	annotationProcessor 'org.projectlombok:lombok:1.18.26'

	testCompileOnly 'org.projectlombok:lombok:1.18.26'
	testAnnotationProcessor 'org.projectlombok:lombok:1.18.26'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	
	// grpc
	implementation 'net.devh:grpc-client-spring-boot-starter:2.14.0.RELEASE'
	compileOnly 'jakarta.annotation:jakarta.annotation-api:1.3.5' // Java 9+ compatibility - Do NOT update to 2.0.0
}

tasks.named('test') {
	useJUnitPlatform()
}

// protobuf compile 설정
protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:${protobufVersion}"
	}
	generatedFilesBaseDir = "$projectDir/src/generated"
	clean {
		delete generatedFilesBaseDir
	}
	plugins {
		grpc {
			artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
		}
	}
	generateProtoTasks {
		all()*.plugins {
			grpc {}
		}
	}
}
```

# Config
- Spring Boot, gRPC client 설정

src/main/resources/application.yml
```yaml
server:  
  port: 8081  
  
spring:  
  application:  
    name: local-grpc-client  
  
grpc:  
  client:  
    local-grpc-server:  
      address: 'static://127.0.0.1:9090' # grpc server address
      enableKeepAlive: true
      keepAliveWithoutCalls: true
      negotiationType: plaintext
```
# Protobuf
- XML, JSON 과 같은 통신시 사용되는 프로토콜
- \*.proto 파일로 되어 있음
- Server, Client 모두 동일한 proto 파일이 있어야 함
- gradle build 시 compile되어 proto 파일에 있는 service, message 등 이 java 파일로 생성됨

src/main/proto/sample.proto
```protobuf
syntax = "proto3";

option java_multiple_files = true;
option java_outer_classname = "SampleProto";
option java_package = "com.gilbert.grpc.proto";

package com.gilbert.grpc;

message SampleRequest { // 객체
  string userId = 1;                   // 필드
  string message = 2;              // 필드
}

message SampleResponse {
  string message = 1;
}

service SampleService { // 클래스
  rpc SampleCall (SampleRequest) returns (SampleResponse) {} // rpc 메서드 (입력 객체) return (출력 객체) 
}
```

# Compiled protobuf
- compile된 파일들

![[Pasted image 20230622114136.png]]

`src/generated/main/grpc/com/gilbert/grpc/proto/SampleServiceGrpc.java`
```java
package com.gilbert.grpc.proto;  
  
import static io.grpc.MethodDescriptor.generateFullMethodName;  
  
/**  
 */
 @Generated(  
    value = "by gRPC proto compiler (version 1.51.0)",  
    comments = "Source: sample.proto")  
@GrpcGenerated  
public final class SampleServiceGrpc {  
  
  private SampleServiceGrpc() {}  
  
  public static final String SERVICE_NAME = "com.gilbert.grpc.SampleService";  
  
  // Static method descriptors that strictly reflect the proto.  
  private static volatile io.grpc.MethodDescriptor<com.gilbert.grpc.proto.SampleRequest,  
      com.gilbert.grpc.proto.SampleResponse> getSampleCallMethod;


...

```

`src/generated/main/java/com/gilbert/grpc/proto/SampleRequest.java`
```java
package com.gilbert.grpc.proto;  
  
/**  
 * Protobuf type {@code com.gilbert.grpc.SampleRequest}  
 */public final class SampleRequest extends  
    GeneratedMessageV3 implements  
    // @@protoc_insertion_point(message_implements:com.gilbert.grpc.SampleRequest)  
    SampleRequestOrBuilder {  
private static final long serialVersionUID = 0L;  
  // Use SampleRequest.newBuilder() to construct.  
  private SampleRequest(GeneratedMessageV3.Builder<?> builder) {  
    super(builder);  
  }  
  private SampleRequest() {  
    userId_ = "";  
    message_ = "";  
  }

...

```

# GrpcController
- localhost:8081/sampleService 로 요청을 보내면 grpcClientService의 sampleCall() 메서드를 호출함

```java
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
```
# GrpcClientService
- `@GrpcClient` 어노테이션을 사용하여 application.yml에 기록된 값을 config로 가져와 사용함 (application.yml: `grpc.client.<key>="local-grpc-server"`)
- proto 파일을 compile하여 생성된 객체들(SampleRequest, SampleResponse, ...)을 사용하여 grpc server의 sampleCall 메서드를 호출
- Stub
    - compile되면서 생성된 일종의 Client(aws sdk 에서 s3client 같은)
    - Stub을 통해서만 gRPC server와 통신해야 함

```java
@Slf4j
@Service
public class GrpcClientService {

    @GrpcClient("local-grpc-server")
    private  SampleServiceGrpc.SampleServiceBlockingStub sampleStub; // Stub class를 가져와서 사용

    public void sampleCall() {
        SampleRequest sampleRequest = SampleRequest.newBuilder()
            .setUserId("gilbert")
            .setMessage("grpc request")
            .build(); // 요청 객체 생성

        try {
            log.info("GrpcClientService#sampleStub.sampleCall - {}", sampleRequest);
            SampleResponse sampleResponse = sampleStub.sampleCall(sampleRequest); //Stub으로 grpc server의 sampleCall 메서드 실행
            log.info("GrpcClientService#sampleResponse - {}", sampleResponse);
        } catch (StatusRuntimeException e) {
            log.error("RPC failed: {}", e.getStatus());
        }
    }
}
```

