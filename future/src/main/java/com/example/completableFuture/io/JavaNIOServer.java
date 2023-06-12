package com.example.completableFuture.io;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JavaNIOServer {
    @SneakyThrows
    public static void main(String[] args) {
        log.info("start server");

        try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
            serverSocket.bind(new InetSocketAddress("localhost", 8080));

            while (true) {
                SocketChannel clientSocket = serverSocket.accept();

                ByteBuffer requestByteBuffer = ByteBuffer.allocateDirect(1024);
                clientSocket.read(requestByteBuffer);
                requestByteBuffer.flip();

                String requestBody = StandardCharsets.UTF_8.decode(requestByteBuffer).toString();

                log.info("request: {}", requestBody);

                String response = "this is server";
                ByteBuffer responseByteBuffer = ByteBuffer.wrap(response.getBytes());
                clientSocket.write(responseByteBuffer);
                clientSocket.close();
            }
        }
    }
}
