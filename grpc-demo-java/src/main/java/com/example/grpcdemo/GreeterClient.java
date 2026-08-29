package com.example.grpcdemo;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import java.util.concurrent.TimeUnit;

public final class GreeterClient {
    private final ManagedChannel channel;
    private final GreeterServiceGrpc.GreeterServiceBlockingStub blockingStub;

    public GreeterClient(String host, int port) {
        channel = Grpc.newChannelBuilderForAddress(host, port, InsecureChannelCredentials.create())
                .build();
        blockingStub = GreeterServiceGrpc.newBlockingStub(channel);
    }

    public String sayHello(String name) {
        HelloRequest request = HelloRequest.newBuilder()
                .setName(name)
                .build();
        HelloReply reply = blockingStub.sayHello(request);
        return reply.getMessage();
    }

    public void countdown(int start) {
        CountdownRequest request = CountdownRequest.newBuilder()
                .setStart(start)
                .build();
        blockingStub.countdown(request)
                .forEachRemaining(reply -> System.out.printf("客户端收到流式响应：%s%n", reply.getMessage()));
    }

    public void shutdown() throws InterruptedException {
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        String mode = args.length > 0 ? args[0] : "hello";
        GreeterClient client = new GreeterClient("localhost", 50051);
        try {
            if ("countdown".equalsIgnoreCase(mode)) {
                int start = args.length > 1 ? Integer.parseInt(args[1]) : 5;
                client.countdown(start);
                return;
            }

            String name = args.length > 1 ? args[1] : "Codex";
            String message = client.sayHello(name);
            System.out.println("客户端收到一元响应：" + message);
        } finally {
            client.shutdown();
        }
    }
}
