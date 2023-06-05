package com.example.future.blocking;

import com.example.completableFuture.blocking.UserBlockingService;
import com.example.completableFuture.blocking.repository.ArticleRepository;
import com.example.completableFuture.blocking.repository.FollowRepository;
import com.example.completableFuture.blocking.repository.ImageRepository;
import com.example.completableFuture.blocking.repository.UserRepository;
import com.example.completableFuture.common.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserBlockingServiceTest {

    private ArticleRepository articleRepository;
    private FollowRepository followRepository;
    private ImageRepository imageRepository;
    private UserRepository userRepository;

    private UserBlockingService userBlockingService;

    @BeforeEach
    void setup() {
        articleRepository = new ArticleRepository();
        followRepository = new FollowRepository();
        imageRepository = new ImageRepository();
        userRepository = new UserRepository();

        userBlockingService = new UserBlockingService(userRepository, articleRepository, imageRepository, followRepository);
    }

    @Test
    void getUserByIdException() {
        String userId = "invalid_user";
        Optional<User> user = userBlockingService.getUserById(userId);
        assertTrue(user.isEmpty());
    }

    @Test
    void getUser() {
        String userId = "1234";

        Optional<User> optionalUser = userBlockingService.getUserById(userId);
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