package com.example.completableFuture.blocking.repository;

import com.example.completableFuture.common.repository.ImageEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class ImageRepository {
    private final Map<String, ImageEntity> imageMap;

    public ImageRepository() {
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
    public Optional<ImageEntity> findById(String id) {
        log.info("ImageReactorRepository.findById: {}", id);
        Thread.sleep(1000);
        return Optional.ofNullable(imageMap.get(id));
    }
}