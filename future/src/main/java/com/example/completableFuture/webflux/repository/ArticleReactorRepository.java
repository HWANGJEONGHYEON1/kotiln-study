package com.example.completableFuture.webflux.repository;

import com.example.completableFuture.common.repository.ArticleEntity;
import com.example.completableFuture.common.repository.UserEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
public class ArticleReactorRepository {
    private static List<ArticleEntity> articleEntities;

    public ArticleReactorRepository() {
        articleEntities = List.of(
                new ArticleEntity("1", "제목1", "내용1", "1234"),
                new ArticleEntity("2", "제목2", "내용2", "1234"),
                new ArticleEntity("3", "제목3", "내용3", "12345"),
                new ArticleEntity("4", "제목4", "내용1", "1234"),
                new ArticleEntity("5", "제목5", "내용2", "1234"),
                new ArticleEntity("6", "제목6", "내용3", "12345"),
                new ArticleEntity("7", "제목7", "내용1", "1234"),
                new ArticleEntity("8", "제목8", "내용2", "1234"),
                new ArticleEntity("9", "제목9", "내용3", "12345")

        );
    }

    @SneakyThrows
    public Flux<ArticleEntity> findAllByUserId(String userId) {
        log.info("ArticleReactorRepository.findAllByUserId: {}", userId);
        return Flux.create(sink -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            articleEntities.stream()
                    .filter(articleEntity -> articleEntity.getUserId().equals(userId))
                    .forEach(articleEntity -> sink.next(articleEntity));
            sink.complete();
        });
    }

    public Flux<ArticleEntity> findAllWithContext() {
        return Flux.deferContextual(contextView -> {
            Optional<UserEntity> userEntityOptional = contextView.getOrEmpty("user");

            if (userEntityOptional.isEmpty()) {
                throw new RuntimeException("user not found");
            }

            return Mono.just(userEntityOptional.get().getId());
        }).flatMap(this::findAllByUserId);
    }
}