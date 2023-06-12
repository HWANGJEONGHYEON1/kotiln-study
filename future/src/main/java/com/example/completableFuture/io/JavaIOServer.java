package com.example.completableFuture.io;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class JavaIOServer {
    @SneakyThrows
    public static void main(String[] args) {
        log.info("start server");

        try (ServerSocket socket = new ServerSocket()) {
            socket.bind(new InetSocketAddress("localhost", 8080));
            while (true) {
                Socket clientSocket = socket.accept();

                byte[] requestBytes = new byte[1024];
                InputStream in = clientSocket.getInputStream();
                in.read(requestBytes);
                log.info("request: {}", new String(requestBytes).trim());

                OutputStream out = clientSocket.getOutputStream();
                String response = "this is server";
                out.write(response.getBytes());
                out.flush();
            }
        }
    }
}
