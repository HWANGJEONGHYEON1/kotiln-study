package com.example.completableFuture.webflux;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class UserFullOperationTest {

    @SneakyThrows
    public static void main(String[] args) {
//        map();
//        flatMap();
//        take();
//        collectList();
        cache();
        Thread.sleep(1000);
    }

    // onNext 값을 받아 값을 변경 mapNotNull은 null 필터링
    public static void map() {
        Flux.range(1, 5)
                .map(i -> i * 2)
                .doOnNext(i -> log.info("value : {}", i))
                .subscribe();

        Flux.range(1, 5)
                .mapNotNull(i -> {
                    if (i % 2 == 0) {
                        return i;
                    }
                    return null;
                })
                .doOnNext(i -> log.info("value : {}", i))
                .subscribe();
    }

    @SneakyThrows
    public static void flatMap() {
        Flux.range(1, 5)
                .flatMap(value -> Flux.range(1, 2)
                        .map(value1 -> value + ","  + value1)
                        .publishOn(Schedulers.parallel()))
                .doOnNext(i -> log.info("value : {}", i))
                .subscribe();
    }

    // n개 까지 onNext에 전파하고 onComplete가 호출 -> skip()과 반대
    // takeLast onComplete 종료 시점까지 대기한 후 앞에 데이터 날림
    // 1,2,3,4,5 -> 2,3,4,5,6 ... "6,7,8,9,10"
    public static void take() {
        Flux.range(1, 10)
                .take(5)
                .doOnNext(i -> log.info("value : {}", i))
                .subscribe();

        Flux.range(1, 10)
                .takeLast(5)
                .doOnNext(i -> log.info("value : {}", i))
                .subscribe();
    }

    // next() 아이템이 내부에 저장되어 list 형태로 만들고 전달
    // flux -> mono 전환할 때 유용
    // 예) 5개의 원소중 나이가 제일 낮은 값을 가져올 때 리스트로 받아 순회 후 추출
    public static void collectList() {
        Flux.range(1, 10)
                .collectList()
                .doOnNext(i -> log.info("value : {}", i))
                .subscribe();
    }

    // 한번만 publisher 실행
    // 그 이후에는 subscribe 한 순간부터 이벤트를 흘려준다.
    public static void cache() {
        var flux = Flux.create(sink -> {
            for (int i = 0; i < 3; i++) {
                log.info("next : {}", i);
                sink.next(i);
            }
            log.info("complete in publisher");
            sink.complete();
        }).cache();

        flux.subscribe(value -> log.info("value1 : {}", value), null, () -> log.info("complete1"));
        flux.subscribe(value -> log.info("value2 : {}", value), null, () -> log.info("complete2"));
    }
}
