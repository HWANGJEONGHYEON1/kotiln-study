package com.example.completableFuture.reactorpattern;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.ast.Selection;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class HttpEventHandler implements EventHandler {
    private final ExecutorService executorService = Executors.newFixedThreadPool(50);
    private final Selector selector;
    private final SocketChannel clientSocket;
    private final MsgCodec msgCodec;

    @SneakyThrows
    public HttpEventHandler(Selector selector, SocketChannel clientSocket) {
        this.selector = selector;
        this.clientSocket = clientSocket;
        this.clientSocket.configureBlocking(false);
        this.clientSocket.register(selector, SelectionKey.OP_READ).attach(this);
        this.msgCodec = new MsgCodec();
    }

    @Override
    public void handle() {
        String request = handleRequest(this.clientSocket);
        log.info("request : {}", request);
        sendResponse(clientSocket, request);
    }

    @SneakyThrows
    private String handleRequest(SocketChannel clientSocket) {
        ByteBuffer requestByteBuffer = ByteBuffer.allocateDirect(1024);
        clientSocket.read(requestByteBuffer);
        return msgCodec.decode(requestByteBuffer);
    }

    @SneakyThrows
    private void sendResponse(SocketChannel clientSocket, String requestBody) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10);

                ByteBuffer responeByteBuffer = msgCodec.encode(requestBody);
                clientSocket.write(responeByteBuffer);
                clientSocket.close();
            } catch (Exception e) { }
        }, executorService);
    }
}
