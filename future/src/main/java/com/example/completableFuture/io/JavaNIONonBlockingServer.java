package com.example.completableFuture.io;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

@Slf4j
public class JavaNIONonBlockingServer {
    @SneakyThrows
    public static void main(String[] args) {
        log.info("start server");

        try (ServerSocketChannel serverSocket = ServerSocketChannel.open();
             Selector selector = Selector.open()) {
            serverSocket.bind(new InetSocketAddress("localhost", 8080));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();

                while (selectionKeys.hasNext()) {
                    SelectionKey key = selectionKeys.next();
                    selectionKeys.remove();

                    if (key.isAcceptable()) {
                        SocketChannel clientSocket = ((ServerSocketChannel) key.channel()).accept();
                        clientSocket.configureBlocking(false);
                        clientSocket.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        SocketChannel clientSocket = (SocketChannel) key.channel();

                        String requestBody = handleRequest(clientSocket);
                        sendResponse(clientSocket, requestBody);
                    }
                }
            }
        }
    }

    private static String handleRequest(SocketChannel clientSocket) throws IOException, InterruptedException {
        ByteBuffer requestByteBuffer = ByteBuffer.allocateDirect(1024);
        clientSocket.read(requestByteBuffer);
        requestByteBuffer.flip();

        String requestBody = StandardCharsets.UTF_8.decode(requestByteBuffer).toString();

        log.info("request: {}", requestBody);
        return requestBody;

    }

    @SneakyThrows
    private static void sendResponse(SocketChannel socketChannel, String requestBody) {
        Thread.sleep(10);
        String content = "received : " + requestBody;

        ByteBuffer wrap = ByteBuffer.wrap(content.getBytes());
        socketChannel.write(wrap);
        socketChannel.close();
    }
}
