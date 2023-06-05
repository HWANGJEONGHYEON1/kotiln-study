package com.example.completableFuture.reactive.reactive;

import lombok.SneakyThrows;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {

        // create publisher
        SimpleColdPublisher simpleColdePublisher = new SimpleColdPublisher();


        SimpleNamedSubscriber<Integer> subscriber = new SimpleNamedSubscriber<>("subscriber1");
        simpleColdePublisher.subscribe(subscriber);

        Thread.sleep(5000);
        SimpleNamedSubscriber<Integer> subscriber2 = new SimpleNamedSubscriber<>("subscriber2");
        simpleColdePublisher.subscribe(subscriber2);

    }
}
