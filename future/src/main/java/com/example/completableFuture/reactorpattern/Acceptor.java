package com.example.completableFuture.reactorpattern;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@RequiredArgsConstructor
@Slf4j
public class Acceptor implements EventHandler {

    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;

    @Override
    @SneakyThrows
    public void handle() {
        SocketChannel clientSocket = serverSocketChannel.accept();
        log.info("client :{}", clientSocket);

//       TcpEventHandler tcpEventHandler = new TcpEventHandler(selector, clientSocket);
       new HttpEventHandler(selector, clientSocket);
    }
}
