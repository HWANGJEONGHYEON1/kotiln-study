package com.example.completableFuture.reactive.reactive;

import lombok.SneakyThrows;

public class HotMain {

    @SneakyThrows
    public static void main(String[] args) {
        var publisher = new SimpleHotPublisher();

        var subscriber = new SimpleNamedSubscriber<>("subscriber1");
        publisher.subscribe(subscriber);

        Thread.sleep(1000);
        subscriber.cancel();
//        publisher.shutdown();
        var subscriber2 = new SimpleNamedSubscriber<>("subscriber2");
        var subscriber3 = new SimpleNamedSubscriber<>("subscriber3");
        publisher.subscribe(subscriber2);
        publisher.subscribe(subscriber3);

        Thread.sleep(5000);
        subscriber2.cancel();
        subscriber3.cancel();
//        publisher.shutdown();

        Thread.sleep(1000);
        var subscriber4 = new SimpleNamedSubscriber<>("subscriber4");
        publisher.subscribe(subscriber4);

        Thread.sleep(5000);
        subscriber4.cancel();

        publisher.shutdown();

    }
}
