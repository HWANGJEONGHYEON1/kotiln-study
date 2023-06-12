package com.example.completableFuture.io;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class JavaIOClient {
    public static void main(String[] args) {
        log.info("start client");
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", 8080));

            OutputStream outputStream = socket.getOutputStream();
            String requestBody = "This is client";
            outputStream.write(requestBody.getBytes());
            outputStream.flush();

            InputStream inputStream = socket.getInputStream();
            byte[] responseByte = new byte[1024];
            inputStream.read(responseByte);
            log.info("result = {}", new String(responseByte).trim());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("finish client");
    }
}
