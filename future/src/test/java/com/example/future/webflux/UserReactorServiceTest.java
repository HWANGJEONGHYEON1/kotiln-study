
package com.example.future.webflux;

import com.example.completableFuture.common.domain.User;
import com.example.completableFuture.future.UserFutureService;
import com.example.completableFuture.future.repository.ArticleFutureRepository;
import com.example.completableFuture.future.repository.FollowFutureRepository;
import com.example.completableFuture.future.repository.ImageFutureRepository;
import com.example.completableFuture.future.repository.UserFutureRepository;
import com.example.completableFuture.webflux.UserReactorService;
import com.example.completableFuture.webflux.repository.ArticleReactorRepository;
import com.example.completableFuture.webflux.repository.FollowReactorRepository;
import com.example.completableFuture.webflux.repository.ImageReactorRepository;
import com.example.completableFuture.webflux.repository.UserReactorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class UserReactorServiceTest {

    private ArticleReactorRepository articleRepository;
    private FollowReactorRepository followRepository;
    private ImageReactorRepository imageRepository;
    private UserReactorRepository userRepository;

    private UserReactorService userFutureService;

    @BeforeEach
    void setup() {
        articleRepository = new ArticleReactorRepository();
        followRepository = new FollowReactorRepository();
        imageRepository = new ImageReactorRepository();
        userRepository = new UserReactorRepository();

        userFutureService = new UserReactorService(userRepository, articleRepository, imageRepository, followRepository);
    }

    @Test
    void getUserByIdException() throws ExecutionException, InterruptedException {
        String userId = "invalid_user";
        Optional<User> user = userFutureService.getUserById(userId).blockOptional();
        assertTrue(user.isEmpty());
    }

    @Test
    void getUser() throws ExecutionException, InterruptedException {
        String userId = "1234";

        User user = userFutureService.getUserById(userId).block();

        assertEquals(user.getName(), "abc");
        assertEquals(user.getAge(), 27);

        assertFalse(user.getProfileImage().isEmpty());
        var image = user.getProfileImage().get();
        assertEquals(image.getName(), "profileImage");
        assertEquals(image.getUrl(), "sample.jpg");
        assertEquals(image.getId(), "image#1000");
    }
}