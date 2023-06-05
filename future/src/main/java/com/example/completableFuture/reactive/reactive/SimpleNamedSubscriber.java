package com.example.completableFuture.reactive.reactive;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;

@Slf4j
public class SimpleNamedSubscriber<T> implements Flow.Subscriber<T> {

    private Flow.Subscription subscription;
    private final String name;

    public SimpleNamedSubscriber(String name) {
        this.name = name;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        this.subscription.request(1);
        log.info("onSubscribe");
    }

    @Override
    public void onNext(T item) {
        log.info("name: {}, onNext: {}", name, item);
        this.subscription.request(2);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("onError : {}", throwable.getMessage());
    }

    @Override
    public void onComplete() {
        log.info("onComplete");
    }

    public void cancel() {
        this.subscription.cancel();
    }
}
