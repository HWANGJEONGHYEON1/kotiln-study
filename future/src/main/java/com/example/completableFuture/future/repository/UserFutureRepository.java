package com.example.completableFuture.future.repository;

import com.example.completableFuture.common.repository.UserEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class UserFutureRepository {
    private final Map<String, UserEntity> userMap;

    public UserFutureRepository() {
        var user = new UserEntity("1234", "abc", 27, "image#1000");
        userMap = Map.of("1234", user);
    }

    @SneakyThrows
    public CompletableFuture<Optional<UserEntity>> findById(String userId) {
        log.info("UserFutureRepository.findById: {}", userId);
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Optional.ofNullable(userMap.get(userId));
        });
    }
}
