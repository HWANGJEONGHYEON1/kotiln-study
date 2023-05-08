package com.example.future.common.domain;

import lombok.Data;

@Data
public class User {
    private final String id;
    private final String name;
    private final int age;
    private final Optional<Image> profileImage;
    private final List<Article> articleList;
    private final Long followCount;

}