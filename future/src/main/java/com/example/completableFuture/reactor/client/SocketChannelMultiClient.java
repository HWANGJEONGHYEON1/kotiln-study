package com.example.completableFuture.reactor.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SocketChannelMultiClient {

    @SneakyThrows
    public static void main(String[] args) {
        List<CompletableFuture> completableFutures = new ArrayList<>();
        log.info("start main");
        long start = System.currentTimeMillis();

        var executor = Executors.newFixedThreadPool(50);

        var counter = new AtomicInteger(0);

        for (var i = 0; i < 1000; i++) {
            var future = CompletableFuture.runAsync(() -> {
                try {
                    try (var socketChannel = SocketChannel.open()) {
                        var address = new InetSocketAddress("localhost", 8080);
                        socketChannel.connect(address);

                        String request = "This is client.";
                        ByteBuffer requestBuffer = ByteBuffer.wrap(request.getBytes());
                        socketChannel.write(requestBuffer);

                        ByteBuffer res = ByteBuffer.allocateDirect(1024);
                        while (socketChannel.read(res) > 0) {
                            res.flip();
//                            log.info("resp: {}", StandardCharsets.UTF_8.decode(res));
                            res.clear();
                        }
                        counter.incrementAndGet();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, executor);

            completableFutures.add(future);
        }

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        log.info("end main");

        long end = System.currentTimeMillis();
        log.info("time: {}s", (end - start)/1000.0);
        log.info("count: {}", counter.get());
    }
}
