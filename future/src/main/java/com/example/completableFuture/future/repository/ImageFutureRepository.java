package com.example.completableFuture.future.repository;

import com.example.completableFuture.common.repository.ImageEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ImageFutureRepository {
    private final Map<String, ImageEntity> imageMap;

    public ImageFutureRepository() {
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
    public CompletableFuture<Optional<ImageEntity>> findById(String id) {
        log.info("ImageFutureRepository.findById: {}", id);
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Optional.ofNullable(imageMap.get(id));
        });
    }
}