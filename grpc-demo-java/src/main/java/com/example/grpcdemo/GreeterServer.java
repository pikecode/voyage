package com.example.grpcdemo;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.time.LocalDateTime;

public final class GreeterServer {
    private static final int PORT = 50051;

    private final Server server;

    public GreeterServer() {
        server = Grpc.newServerBuilderForPort(PORT, InsecureServerCredentials.create())
                .addService(new GreeterService())
                .build();
    }

    public void start() throws IOException {
        server.start();
        System.out.printf("gRPC 服务端已启动，监听端口：%d%n", PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("收到关闭信号，正在停止 gRPC 服务端...");
            GreeterServer.this.stop();
        }));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        GreeterServer server = new GreeterServer();
        server.start();
        server.blockUntilShutdown();
    }

    private static final class GreeterService extends GreeterServiceGrpc.GreeterServiceImplBase {
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            String name = request.getName().isBlank() ? "gRPC" : request.getName();
            String message = "你好，" + name + "。这是一条来自 gRPC 服务端的响应，时间：" + LocalDateTime.now();
            HelloReply reply = HelloReply.newBuilder()
                    .setMessage(message)
                    .build();

            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        @Override
        public void countdown(CountdownRequest request, StreamObserver<CountdownReply> responseObserver) {
            int start = request.getStart() <= 0 ? 5 : Math.min(request.getStart(), 10);
            for (int current = start; current >= 0; current--) {
                CountdownReply reply = CountdownReply.newBuilder()
                        .setCurrent(current)
                        .setMessage(current == 0 ? "倒计时结束" : "倒计时：" + current)
                        .build();
                responseObserver.onNext(reply);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    responseObserver.onError(e);
                    return;
                }
            }
            responseObserver.onCompleted();
        }
    }
}
