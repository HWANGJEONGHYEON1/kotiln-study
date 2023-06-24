package com.example.completableFuture.webflux;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Slf4j
public class ErrorHandlingTest {

    /**
     * reactive streams에서 onError가 발생하면 더 이상 진행하지 않음 onError로 전파
     * 처리
     *  또 다른 에러로 변환
     *  고정된 값을 반환
     *  publish를 반환
     *  onComplete로 변환
     * @param args
     */
    @SneakyThrows
    public static void main(String[] args) {
//        errorConsume();
//        errorReturn();
//        errorResume();
//        errorComplete();
//        errorResumeConvert();
//        Thread.sleep(100);
//        errorMap();
        doOnError();
    }

    public static void errorConsume() {
        Flux.error(new RuntimeException("error"))
                .subscribe(value -> log.info("value: {}", value), error -> log.error("error : " + error));
    }

    // 고정 된 값을 사용하기 위해 함수를 실행하면 문제가 될 수 있음 무조건 실행됨..
    public static void errorReturn() {
        Flux.error(new RuntimeException("error"))
                .onErrorReturn(0)
                .subscribe(value -> log.info("value: {}", value), error -> log.error("error : " + error));
    }

    // 에러가 발생했을 때만
    public static void errorResume() {
        Flux.error(new RuntimeException("error"))
                .onErrorResume(new Function<Throwable, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Throwable throwable) {
                        return Flux.just(1,2,3);
                    }
                })
                .subscribe(value -> log.info("value: {}", value), error -> log.error("error : " + error));
    }

    public static void errorComplete() {
        Flux.create(sink -> {
            sink.next(1);
            sink.next(2);
            sink.error(new RuntimeException("error"));
        }).onErrorComplete()
          .subscribe(value -> log.info("value: {}", value), error -> log.error("error : " + error), () -> log.info("complete"));
    }

    public static void errorResumeConvert() {
        Flux.error(new RuntimeException("error"))
                .onErrorResume(e -> Flux.error(new IllegalStateException("new Error1")))
                .subscribe(value -> log.info("value: {}", value), error -> log.error("error : " + error));
    }

    // 저수준의 에러를 고수준의 에러로 변환
    public static void errorMap() {
        Flux.error(new RuntimeException("error"))
                .onErrorMap(e -> new IllegalStateException("new Erro2"))
                .subscribe(value -> log.info("value: {}", value), error -> log.error("error : " + error));
    }

    // 에러 처리르 하기전 로깅 가능
    public static void doOnError() {
        Flux.error(new RuntimeException("error"))
                .doOnError(e -> log.error("e : {}" + e))
                .onErrorMap(e -> new IllegalStateException("new Erro2"))
                .subscribe(value -> log.info("value: {}", value), error -> log.error("error : " + error));
    }
}
