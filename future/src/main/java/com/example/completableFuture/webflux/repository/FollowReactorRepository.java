package com.example.completableFuture.webflux.repository;

import com.example.completableFuture.common.repository.UserEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import javax.swing.text.html.Option;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class FollowReactorRepository {
    private Map<String, Long> userFollowCountMap;

    public FollowReactorRepository() {
        userFollowCountMap = Map.of("1234", 1000L);
    }

    @SneakyThrows
    public Mono<Long> countByUserId(String userId) {
        log.info("FollowReactorRepository.countByUserId: {}", userId);
        return Mono.create(sink -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            sink.success(userFollowCountMap.getOrDefault(userId, 0L));
        });
    }

    public Mono<Long> countByContext() {
        return Mono.deferContextual(contextView -> {
            Optional<UserEntity> user = contextView.getOrEmpty("user");
            if (user.isEmpty()) {
                throw new RuntimeException("not found user");
            }

            return Mono.just(user.get().getId());
        }).flatMap(this::countByUserId);
    }
}
