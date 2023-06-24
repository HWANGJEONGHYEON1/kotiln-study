package com.example.completableFuture.webflux;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class ExampleTest {

    public static void main(String[] args) {
//        immediate();
//        single();
//        parallel();
//        boundedElastic();
        publishOn();
    }

    public static void immediate() {
        Flux.create(sink -> {
            for (int i = 0; i < 5; i++) {
                log.info("next : {}", i);
                sink.next(i);
            }
        }).subscribeOn(Schedulers.immediate())
        .subscribe(value -> {
            log.info("value : {}", value);
        });
    }

    /**
     * 캐싱된 하나의 스레드풀을 제공
     */
    @SneakyThrows
    public static void single() {
        for (int i = 0; i < 100; i++) {
            int idx = i;
            Flux.create(sink -> {
                log.info("next : {}", idx);
                sink.next(idx);
            }).subscribeOn(Schedulers.single())
            .subscribe(value -> {
                log.info("value : {}", value);
            });
        }
        Thread.sleep(100);
    }

    /**
     * 캐싱된 n개의 크기의 스레드풀 제공
     * 기본 cpu 코어 수만큼
     */
    @SneakyThrows
    public static void parallel() {
        for (int i = 0; i < 100; i++) {
            int idx = i;
            Flux.create(sink -> {
                log.info("next : {}", idx);
                sink.next(idx);
            }).subscribeOn(Schedulers.parallel())
                    .subscribe(value -> {
                        log.info("value : {}", value);
                    });
        }
        Thread.sleep(100);
    }

    /**
     * 캐싱된 고정되지 않은 스레드풀 사용
     * 재사용할 수 있는 스레드가 있으면 사용, 없으면 새로 생성
     * 특정 시간(기본 60) 사용하지 않으면 제거
     * 생성 가능한 스레드 수 제한 (기본 cpu * 10)
     * i/o 일 때 적합
     */
    @SneakyThrows
    public static void boundedElastic() {
        for (int i = 0; i < 100; i++) {
            int idx = i;
            Flux.create(sink -> {
                log.info("next : {}", idx);
                sink.next(idx);
            }).subscribeOn(Schedulers.boundedElastic())
                    .subscribe(value -> {
                        log.info("value : {}", value);
                    });
        }
        Thread.sleep(100);
    }

    @SneakyThrows
    public static void publishOn() {
        Flux.create(sink -> {
            for (int i = 0; i < 5; i++) {
                log.info("next : {}", i);
                sink.next(i);
            }
        }).publishOn(Schedulers.single())
                .doOnNext(item -> log.info("doOnNext: {}", item))
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(item -> log.info("doOnNext2: {}", item))
                .subscribe(value -> {
                    log.info("value : {}", value);
                });
        Thread.sleep(100);
    }

}
