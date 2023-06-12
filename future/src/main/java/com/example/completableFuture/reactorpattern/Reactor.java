package com.example.completableFuture.reactorpattern;

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Reactor implements Runnable {
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ServerSocketChannel serverSocket;
    private final Selector selector;
    private final EventHandler acceptor;

    @SneakyThrows
    public Reactor(int port) {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        acceptor = new Acceptor(selector, serverSocket);
        serverSocket.bind(new InetSocketAddress("localhost", port));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT).attach(acceptor);

    }

    @Override
    public void run() {
        executorService.submit(() -> {
            while (true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    dispatch(key);
                }
            }
        });
    }

    private void dispatch(SelectionKey selectionKey) {
        EventHandler eventHandler = (EventHandler) selectionKey.attachment();

        if (selectionKey.isReadable() || selectionKey.isAcceptable()) {
            eventHandler.handle();
        }
    }
}