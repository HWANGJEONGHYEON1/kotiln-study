package com.example.completableFuture.webflux.repository;

import com.example.completableFuture.common.repository.ImageEntity;
import com.example.completableFuture.common.repository.UserEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ImageReactorRepository {
    private final Map<String, ImageEntity> imageMap;

    public ImageReactorRepository() {
        imageMap = Map.of(
                "image#1000",
                new ImageEntity(
                        "image#1000",
                        "profileImage",
                        "sample.jpg"
                )
        );
    }

    @SneakyThrows
    public Mono<ImageEntity> findById(String id) {
        log.info("ImageReactorRepository.findById: {}", id);
        return Mono.create(sink -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ImageEntity imageEntity = imageMap.get(id);
            if (imageEntity == null) {
                sink.error(new RuntimeException("image not found"));
            } else {
                sink.success(imageEntity);
            }
        });
    }

    public Mono<ImageEntity> findImageContext() {
        return Mono.deferContextual(contextView -> {
            Optional<UserEntity> userEntityOptional = contextView.getOrEmpty("user");

            if (userEntityOptional.isEmpty()) {
                throw new RuntimeException("user not found");
            }

            return Mono.just(userEntityOptional.get().getProfileImageId());
        }).flatMap(this::findById);
    }
}