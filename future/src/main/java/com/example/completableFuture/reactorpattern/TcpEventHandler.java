package com.example.completableFuture.reactorpattern;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class TcpEventHandler implements EventHandler {
    private static ExecutorService executorService = Executors.newFixedThreadPool(50);
    private final Selector selector;
    private final SocketChannel socketChannel;

    @SneakyThrows
    public TcpEventHandler(Selector selector, SocketChannel socketChannel) {
        this.selector = selector;
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        this.socketChannel.register(selector, SelectionKey.OP_READ).attach(this);
    }

    @Override
    public void handle() {
        String request = handleRequest(socketChannel);
        log.info("requestBody: {}", request);
        sendResponse(socketChannel, request);
    }

    @SneakyThrows
    private static String handleRequest(SocketChannel clientSocket) {
        ByteBuffer requestByteBuffer = ByteBuffer.allocateDirect(1024);
        clientSocket.read(requestByteBuffer);

        requestByteBuffer.flip();
        String requestBody = StandardCharsets.UTF_8.decode(requestByteBuffer).toString();
        log.info("request: {}", requestBody);

        return requestBody;
    }

    @SneakyThrows
    private static void sendResponse(SocketChannel clientSocket, String requestBody) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10);

                String content = "received: " + requestBody;
                ByteBuffer responeByteBuffer = ByteBuffer.wrap(content.getBytes());
                clientSocket.write(responeByteBuffer);
                clientSocket.close();
            } catch (Exception e) { }
        }, executorService);
    }
}
