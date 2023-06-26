package com.example.completableFuture.webflux;

import com.example.completableFuture.common.domain.Article;
import com.example.completableFuture.common.domain.Image;
import com.example.completableFuture.common.domain.User;
import com.example.completableFuture.common.repository.UserEntity;
import com.example.completableFuture.future.repository.ArticleFutureRepository;
import com.example.completableFuture.future.repository.FollowFutureRepository;
import com.example.completableFuture.future.repository.ImageFutureRepository;
import com.example.completableFuture.future.repository.UserFutureRepository;
import com.example.completableFuture.webflux.repository.ArticleReactorRepository;
import com.example.completableFuture.webflux.repository.FollowReactorRepository;
import com.example.completableFuture.webflux.repository.ImageReactorRepository;
import com.example.completableFuture.webflux.repository.UserReactorRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class UserReactorService {
    private final UserReactorRepository userRepository;
    private final ArticleReactorRepository articleRepository;
    private final ImageReactorRepository imageRepository;
    private final FollowReactorRepository followRepository;

    @SneakyThrows
    public Mono<User> getUserById(String id) {
        return userRepository.findById(id)
                .flatMap(this::getUser);
    }

    @SneakyThrows
    private Mono<User> getUser(UserEntity user) {

        Context context = Context.of("user", user);
        var imageMono = imageRepository.findImageContext()
                .map(imageEntity -> new Image(imageEntity.getId(), imageEntity.getName(), imageEntity.getUrl()))
                .onErrorReturn(new EmptyImage()).contextWrite(context);

        var articleMono = articleRepository.findAllWithContext()
                .map(articleEntity ->
                        new Article(
                                articleEntity.getId(),
                                articleEntity.getTitle(),
                                articleEntity.getContent(),
                                articleEntity.getUserId()
                        )).collectList().contextWrite(context);


        var followCountMono = followRepository.countByContext()
                .map(count -> count).contextWrite(context);

        return Flux.mergeSequential(imageMono, articleMono, followCountMono)
                .collectList()
                .map(resultList -> {
                    Image image = (Image) resultList.get(0);
                    List<Article> articles = (List<Article>) resultList.get(1);
                    Long followCount = (Long) resultList.get(2);

                    Optional<Image> imageOptional = Optional.empty();

                    if (!(image instanceof EmptyImage)) {
                        imageOptional = Optional.of(image);
                    }
                    return new User(
                                    user.getId(),
                                    user.getName(),
                                    user.getAge(),
                                    imageOptional,
                                    articles,
                                    followCount
                            );
                });
    }
}