package com.example.repeat;

import lombok.Data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Flow;

public class FixedIntPublisher implements Flow.Publisher<FixedIntPublisher.Result> {
    @Override
    public void subscribe(Flow.Subscriber<? super FixedIntPublisher.Result> subscriber) {
        var numbers = Collections.synchronizedSet(new HashSet<>(List.of(1,2,3,4,5,6,7)));

        Iterator<Integer> iterator = numbers.iterator();
        IntSubscription intSubscription = new IntSubscription(subscriber, iterator);
        subscriber.onSubscribe(intSubscription);
    }

    @Data
    public static class Result {
        private final Integer value;
        private final Integer requestCount;
    }
}
