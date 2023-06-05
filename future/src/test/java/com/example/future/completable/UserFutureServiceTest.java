
package com.example.future.completable;

import com.example.completableFuture.common.domain.User;
import com.example.completableFuture.future.UserFutureService;
import com.example.completableFuture.future.repository.ArticleFutureRepository;
import com.example.completableFuture.future.repository.FollowFutureRepository;
import com.example.completableFuture.future.repository.ImageFutureRepository;
import com.example.completableFuture.future.repository.UserFutureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class UserFutureServiceTest {

    private ArticleFutureRepository articleRepository;
    private FollowFutureRepository followRepository;
    private ImageFutureRepository imageRepository;
    private UserFutureRepository userRepository;

    private UserFutureService userFutureService;

    @BeforeEach
    void setup() {
        articleRepository = new ArticleFutureRepository();
        followRepository = new FollowFutureRepository();
        imageRepository = new ImageFutureRepository();
        userRepository = new UserFutureRepository();

        userFutureService = new UserFutureService(userRepository, articleRepository, imageRepository, followRepository);
    }

    @Test
    void getUserByIdException() throws ExecutionException, InterruptedException {
        String userId = "invalid_user";
        Optional<User> user = userFutureService.getUserById(userId).get();
        assertTrue(user.isEmpty());
    }

    @Test
    void getUser() throws ExecutionException, InterruptedException {
        String userId = "1234";

        Optional<User> optionalUser = userFutureService.getUserById(userId).get();
        assertTrue(optionalUser.isPresent());

        var user = optionalUser.get();
        assertEquals(user.getName(), "abc");
        assertEquals(user.getAge(), 27);

        assertFalse(user.getProfileImage().isEmpty());
        var image = user.getProfileImage().get();
        assertEquals(image.getName(), "profileImage");
        assertEquals(image.getUrl(), "sample.jpg");
        assertEquals(image.getId(), "image#1000");
    }
}