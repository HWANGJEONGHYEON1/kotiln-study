package com.example.completableFuture.reactive.reactive;

import lombok.SneakyThrows;

public class Main {

    /**
     * HotPublisher
     * subscriber가 없더라도, 데이터를 생성하고 stream에 push한다.
     * 트위터 게시글 읽기, 공유리소스 변화
     * 여러 subscriber에게 동일한 데이터 전달
     *
     * ColdPublisher
     * subscriber가 시작되는 순간부터 데이터전달
     * 파일 읽기, web api
     * subscriber에 따라 독립적인 스트림 제공
     * @param args
     */

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
