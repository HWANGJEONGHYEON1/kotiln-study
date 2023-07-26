package com.example.repeat;

import java.util.concurrent.Flow;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        FixedIntPublisher publisher = new FixedIntPublisher();
        Flow.Subscriber objectRequestNSubscriber = new RequestNSubscriber<>(3);
        publisher.subscribe(objectRequestNSubscriber);

        Thread.sleep(100);
    }
}
