package com.example.completableFuture.reactive.reactive.hot;

import com.example.completableFuture.reactive.reactive.SimpleNamedSubscriber;
import lombok.SneakyThrows;

public class SimpleHotPublisherMain {

    @SneakyThrows
    public static void main(String[] args) {
        var publisher = new SimpleHotPublisher();
        Thread.sleep(1000);

        var subscriber1 = new SimpleNamedSubscriber<>("subscriber1");
        publisher.subscribe(subscriber1);
        Thread.sleep(5000);
        subscriber1.cancel();

        var subscriber2 = new SimpleNamedSubscriber<>("subscriber2");
        var subscriber3 = new SimpleNamedSubscriber<>("subscriber3");
        publisher.subscribe(subscriber2);
        publisher.subscribe(subscriber3);

        Thread.sleep(5000);
        subscriber2.cancel();
        subscriber3.cancel();

        Thread.sleep(1000);
        var subscriber4 = new SimpleNamedSubscriber<>("subscriber4");
        publisher.subscribe(subscriber4);

        Thread.sleep(4000);
        subscriber4.cancel();

        publisher.shutdown();
    }
}
