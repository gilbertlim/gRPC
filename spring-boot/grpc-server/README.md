# grpc-server-spring-boot-starter
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
    id 'idea'
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
	implementation 'org.springframework.boot:spring-boot-starter'
	compileOnly 'org.projectlombok:lombok:1.18.26'
	annotationProcessor 'org.projectlombok:lombok:1.18.26'

	testCompileOnly 'org.projectlombok:lombok:1.18.26'
	testAnnotationProcessor 'org.projectlombok:lombok:1.18.26'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	
	// grpc
	implementation 'net.devh:grpc-server-spring-boot-starter:2.14.0.RELEASE'
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

idea {
  module {
    sourceDirs += file("src/generated/main/java")
    sourceDirs += file("src/generated/main/grpc")
    generatedSourceDirs += file("src/generated/main/java")
    generatedSourceDirs += file("src/generated/main/grpc")
  }
}
```

# Config
- Spring Boot, gRPC server 설정

src/main/resources/application.yml
```yaml
server:  
  port: 8080  
  
spring:  
  application:  
    name: local-grpc-server  

# grpc
grpc:  
  server:  
    port: 9090
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

![[Pasted image 20230622102651.png]]

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

# GrpcService
- `@GrpcService` 어노테이션을 사용하여 application.yml에 기록된 값을 config로 가져와 사용함 (application.yml: `grpc.server`)
    - 어노테이션을 사용하면 Spring Boot 이 뜰 때, gRPC Server 또한 자동으로 뜸
- proto 파일을 compile하여 생성된 객체들(sampleResponse, ...)을 사용하여 응답
- proto 파일이 compile 되면서 생성된  `SampleServiceGrpc` 클래스의 `SampleServiceImplBase` 추상 클래스를 상속받아 구현

```java
@Slf4j  
@GrpcService  
public class SampleService extends SampleServiceGrpc.SampleServiceImplBase { // compile 된 ServiceGrpc class를 상속받아 사용
  
    @Override  
    public void sampleCall(SampleRequest request, StreamObserver<SampleResponse> responseObserver) { // 메서드 오버라이딩 방식으로 구현
        log.info("SampleService#sampleCall - {}, {}", request.getUserId(), request.getMessage());  
  
        SampleResponse sampleResponse = SampleResponse.newBuilder()  
            .setMessage("response from grpc service")  
            .build();  // 응답 객체 생성
  
        responseObserver.onNext(sampleResponse);  // 응답 작성
        responseObserver.onCompleted();  // 응답
        log.info("SampleService#sampleCall - completed");  
    }  
  
}
```

