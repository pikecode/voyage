# Java gRPC Demo

这是一个最小可运行的 Java gRPC 示例，用来演示：

- 用 `.proto` 定义服务接口和消息结构
- Maven 自动生成 Java gRPC 代码
- 启动服务端并通过客户端调用
- 一元 RPC 和服务端流式 RPC 的区别

## 环境要求

- JDK 17+
- Maven 3.9+

## 编译

```bash
mvn clean compile
```

## 启动服务端

```bash
mvn exec:java -Dexec.mainClass=com.example.grpcdemo.GreeterServer
```

服务端默认监听：

```text
localhost:50051
```

## 运行客户端

另开一个终端：

```bash
mvn exec:java -Dexec.mainClass=com.example.grpcdemo.GreeterClient -Dexec.args="hello 张三"
```

预期输出类似：

```text
客户端收到一元响应：你好，张三。这是一条来自 gRPC 服务端的响应，时间：...
```

## 运行服务端流式调用

另开一个终端：

```bash
mvn exec:java -Dexec.mainClass=com.example.grpcdemo.GreeterClient -Dexec.args="countdown 5"
```

预期输出类似：

```text
客户端收到流式响应：倒计时：5
客户端收到流式响应：倒计时：4
客户端收到流式响应：倒计时：3
客户端收到流式响应：倒计时：2
客户端收到流式响应：倒计时：1
客户端收到流式响应：倒计时结束
```

## 核心文件

- `src/main/proto/greeter.proto`：服务契约
- `src/main/java/com/example/grpcdemo/GreeterServer.java`：服务端
- `src/main/java/com/example/grpcdemo/GreeterClient.java`：客户端

## 工程原则说明

- KISS：只保留一元 RPC 和服务端流式 RPC，覆盖 gRPC 最核心的两种体验。
- YAGNI：不加入 Spring Boot、鉴权、数据库、客户端流、双向流等非必要能力。
- SOLID：服务契约、服务端实现、客户端调用分离。
- DRY：请求和响应结构统一由 `.proto` 生成，避免手写重复 DTO。
