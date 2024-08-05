package org.example.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.model.PlayerCreateRequestDto;
import org.example.model.PlayerUpdateRequestDto;
import org.example.tests.BaseTest.Editor;
import org.example.util.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerApi {

    private static final Logger logger = LoggerFactory.getLogger(PlayerApi.class);
    private static final String BASE_PATH = "/player";

    public static Map<String, Object> convertToMap(Object obj) {
        return Stream.of(obj.getClass().getDeclaredFields())
                .filter(field -> {
                    field.setAccessible(true);
                    try {
                        return field.get(obj) != null; // Skip fields that are not set (null values)
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toMap(Field::getName, field -> {
                    try {
                        return field.get(obj);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    @Step
    public Response createPlayer(Editor editor, PlayerCreateRequestDto player) {
        String path = BASE_PATH + "/create/" + editor.getValue();
        return ApiClient.get(path, convertToMap(player));
    }

    @Step("Delete player with ID {playerId} by editor {editor}")
    public Response deletePlayer(Editor editor, long playerId) {
        String path = BASE_PATH + "/delete/" + editor.getValue();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("playerId", playerId);

        return ApiClient.delete(path, requestBody);
    }

    @Step
    public Response getPlayerById(long playerId) {
        String path = BASE_PATH + "/get";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("playerId", playerId);

        return ApiClient.post(path, requestBody);
    }

    @Step
    public Response getPlayerByIncorrectIdFormat(String playerId) {
        String path = BASE_PATH + "/get";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("playerId", playerId);

        return ApiClient.post(path, requestBody);
    }

    @Step
    public Response getAllPlayers() {
        String path = BASE_PATH + "/get/all";
        return ApiClient.get(path);
    }

    @Step
    public Response updatePlayer(String editor, long playerId, PlayerUpdateRequestDto player) {
        String path = BASE_PATH + "/update/" + editor + "/" + playerId;
        Map<String, Object> requestBody = new HashMap<>();
        // Only add non-null fields to the request body
        if (player.getAge() != 0) requestBody.put("age", player.getAge());
        if (player.getGender() != null) requestBody.put("gender", player.getGender());
        if (player.getLogin() != null) requestBody.put("login", player.getLogin());
        if (player.getPassword() != null) requestBody.put("password", player.getPassword());
        if (player.getRole() != null) requestBody.put("role", player.getRole());
        if (player.getScreenName() != null) requestBody.put("screenName", player.getScreenName());

        return ApiClient.patch(path, requestBody);
    }
}