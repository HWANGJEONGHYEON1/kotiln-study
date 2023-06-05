package com.example.completableFuture.future;

import com.example.completableFuture.common.domain.Article;
import com.example.completableFuture.common.domain.Image;
import com.example.completableFuture.common.domain.User;
import com.example.completableFuture.common.repository.UserEntity;
import com.example.completableFuture.future.repository.ArticleFutureRepository;
import com.example.completableFuture.future.repository.FollowFutureRepository;
import com.example.completableFuture.future.repository.ImageFutureRepository;
import com.example.completableFuture.future.repository.UserFutureRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class UserFutureService {
    private final UserFutureRepository userRepository;
    private final ArticleFutureRepository articleRepository;
    private final ImageFutureRepository imageRepository;
    private final FollowFutureRepository followRepository;

    @SneakyThrows
    public CompletableFuture<Optional<User>> getUserById(String id) {
        return userRepository.findById(id)
                .thenComposeAsync(this::getUser);
    }

    @SneakyThrows
    private CompletableFuture<Optional<User>> getUser(Optional<UserEntity> userEntityOptional) {
        if (userEntityOptional.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        UserEntity user = userEntityOptional.get();

        var imageFuture = imageRepository.findById(user.getProfileImageId())
                .thenApplyAsync(imageOptionalEntity -> imageOptionalEntity.map(imageEntity -> new Image(imageEntity.getId(), imageEntity.getName(), imageEntity.getUrl())));

        var articlesFuture = articleRepository.findAllByUserId(user.getId())
                .thenApplyAsync(articleEntities -> articleEntities
                        .stream()
                        .map(articleEntity ->
                        new Article(
                                articleEntity.getId(),
                                articleEntity.getTitle(),
                                articleEntity.getContent(),
                                articleEntity.getUserId()
                        )).collect(Collectors.toList()));


        var followCountFuture = followRepository.countByUserId(user.getId())
                .thenApplyAsync(count -> count);


        return CompletableFuture.allOf(imageFuture, articlesFuture, followCountFuture)
                .thenAcceptAsync(v -> log.info("futures are completed"))
                .thenApplyAsync(v -> {
                    try {
                        var image = imageFuture.get();
                        var articles = articlesFuture.get();
                        var followCount = followCountFuture.get();
                        return Optional.of(
                                new User(
                                        user.getId(),
                                        user.getName(),
                                        user.getAge(),
                                        image,
                                        articles,
                                        followCount
                               ));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}