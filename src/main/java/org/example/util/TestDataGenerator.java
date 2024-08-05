package org.example.util;

import org.example.model.PlayerCreateRequestDto;

import java.util.Random;

public class TestDataGenerator {
    private static final Random random = new Random();

    public static PlayerCreateRequestDto generateValidPlayer() {
        return new PlayerCreateRequestDto()
                .setAge(random.nextInt(44) + 16)
                .setGender(random.nextBoolean() ? "male" : "female")
                .setLogin("user" + System.currentTimeMillis())
                .setPassword("Pass123" + random.nextInt(1000))
                .setRole(random.nextBoolean() ? "admin" : "user")
                .setScreenName("Name_" + System.currentTimeMillis());
    }

    public static PlayerCreateRequestDto generateInvalidPlayer() {
        PlayerCreateRequestDto player = generateValidPlayer();
        // Make the player invalid (e.g., age out of range)
        player.setAge(random.nextInt(100) + 61);
        return player;
    }
}