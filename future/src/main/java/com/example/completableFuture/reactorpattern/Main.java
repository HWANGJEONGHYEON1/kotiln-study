package com.example.completableFuture.reactorpattern;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Reactor> reactors = new ArrayList<>(List.of(new Reactor(8080), new Reactor(8081)));
        reactors.forEach(Reactor::run);
    }
}
