package com.example.completableFuture.reactive.reactive;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;

@Slf4j
public class SimpleHotPublisher implements Flow.Publisher<Integer> {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Future<Void> task;
    private List<Integer> numbers = new ArrayList<>();
    private List<SimpleHotSubscription> subscriptions = new ArrayList<>();

    public SimpleHotPublisher() {
        numbers.add(1);

        task = executorService.submit(() -> {
            for (int i = 2; !Thread.interrupted(); i++) {
                numbers.add(i);
                Thread.sleep(100);
                subscriptions.forEach(SimpleHotSubscription::wakeup);
            }

            return null;
        });
    }

    public void shutdown() {
        this.task.cancel(true);
        executorService.shutdown();
    }

    @Override
    public void subscribe(Flow.Subscriber<? super Integer> subscriber) {
        var subscription = new SimpleHotSubscription(subscriber);
        subscriptions.add(subscription);
        subscriber.onSubscribe(subscription);
    }

    private class SimpleHotSubscription implements Flow.Subscription {

        private int offset;
        private int requiredOffset;
        private final Flow.Subscriber<? super Integer> subscriber;
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();

        public SimpleHotSubscription(Flow.Subscriber<? super Integer> subscriber) {
            this.offset = numbers.size() - 1;
            this.requiredOffset = numbers.size() - 1;
            this.subscriber = subscriber;
        }

        @Override
        public void request(long n) {
            requiredOffset += n;

            executorService.submit(() -> {
                while (offset < requiredOffset && offset < numbers.size()) {
                    var item = numbers.get(offset);
                    subscriber.onNext(item);
                    offset++;
                }
            });
        }

        @Override
        public void cancel() {
            this.subscriber.onComplete();

            if (subscriptions.contains(this)) {

                subscriptions.remove(this);
            }
            executorService.shutdown();
        }

        public void wakeup() {
            executorService.submit(() -> {
                while (offset < requiredOffset && offset < numbers.size()) {
                    var item = numbers.get(offset);
                    subscriber.onNext(item);
                    offset++;
                }
            });
        }
    }
}
