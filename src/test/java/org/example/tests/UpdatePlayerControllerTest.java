package org.example.tests;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.restassured.response.Response;
import org.example.model.*;
import org.example.util.TestDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Random;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.example.tests.BaseTest.Editor.ADMIN;
import static org.example.tests.BaseTest.Editor.SUPERVISOR;
import static org.testng.Assert.*;

public class UpdatePlayerControllerTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(UpdatePlayerControllerTest.class);
    private static final String SCHEMA_PATH = "schemas/update_player_schema.json";
    private static final String ERROR_SCHEMA_PATH = "schemas/error_response_schema.json";
    private final ThreadLocal<PlayerCreateResponseDto> createdPlayers = new ThreadLocal<>();


    @BeforeMethod
    public void setUp() {
        super.setUp();
        PlayerCreateRequestDto playerCreateRequestDto = TestDataGenerator.generateValidPlayer();
        Allure.step("Generated valid player: " + playerCreateRequestDto);

        logger.info("Check the status code and validate the json response by schema");
        Response response = playerApi.get().createPlayer(ADMIN, playerCreateRequestDto);
        Allure.step("Response SC: " + response.getStatusCode());
        try {
        PlayerCreateResponseDto playerResponse = response.as(PlayerCreateResponseDto.class);
        createdPlayers.set(playerResponse);
        }
        catch (IllegalStateException e){
            reportMessage(logger, "Due to the issue with Player creation, the response is absent " + e);
        }
    }

    @Test
    @Issue("JIRA-1234")
    @Description("Issue with player creation")
    public void testUpdatePlayerWithValidData() {
        int newAge =new Random().nextInt(44) + 16;
        String newName = "updatedScreenName" + currentTimeMillis();
        reportMessage(logger, format("Update Player with new valid data: age [%d] and screenName [%s]", newAge, newName));
        PlayerUpdateRequestDto updateRequest = new PlayerUpdateRequestDto()
                .setAge(newAge)
                .setScreenName(newName);

        reportMessage(logger, "Update the Player. Validate the SC and schema corresponding");
        PlayerCreateResponseDto createdPlayer = createdPlayers.get();
        Response response = playerApi.get().updatePlayer(SUPERVISOR.value, createdPlayer.getId(), updateRequest);
        response.then().statusCode(SC_OK).body(matchesJsonSchemaInClasspath(SCHEMA_PATH));

        reportMessage(logger, "Validate the response values");
        PlayerUpdateResponseDto updatedPlayer = response.as(PlayerUpdateResponseDto.class);
        assertEquals(updatedPlayer.getAge(), updateRequest.getAge(), "Age should be updated");
        assertEquals(updatedPlayer.getScreenName(), updateRequest.getScreenName(), "Screen name should be updated");
        assertEquals(updatedPlayer.getId(), createdPlayer.getId(), "Player ID should not change");
        assertEquals(updatedPlayer.getRole(), createdPlayer.getRole(), "Player role should not change");
        assertEquals(updatedPlayer.getGender(), createdPlayer.getGender(), "Player gender should not change");
    }

    @Test
    public void testUpdatePlayerWithInvalidAge() {
        reportMessage(logger, "Update Player with new invalid data");
        PlayerUpdateRequestDto updateRequest = new PlayerUpdateRequestDto().setAge(10);

        reportMessage(logger, "Update the Player. Expected status code 400 for invalid age");
        PlayerCreateResponseDto createdPlayer = createdPlayers.get();
        Response response = playerApi.get().updatePlayer(ADMIN.value, createdPlayer.getId(), updateRequest);
        response.then().statusCode(SC_BAD_REQUEST).body(matchesJsonSchemaInClasspath(ERROR_SCHEMA_PATH));

        reportMessage(logger, "Check that error message is present");
        ErrorResponseDto errorResponse = response.as(ErrorResponseDto.class);
        assertNotNull(errorResponse.getError(), "Error message should be present");
        assertTrue(errorResponse.getError().contains("age"), "Error message should mention the age problem");
    }
}
