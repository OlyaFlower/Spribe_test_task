package org.example.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.Step;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.example.model.PlayerItemDto;
import org.example.model.PlayerGetAllResponseDto;
import org.example.model.PlayerCreateRequestDto;
import org.example.model.PlayerCreateResponseDto;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static org.apache.http.HttpStatus.*;
import static org.example.tests.BaseTest.Editor.ADMIN;
import static org.example.tests.BaseTest.Editor.SUPERVISOR;
import static org.example.util.TestDataGenerator.generateValidPlayer;
import static org.testng.Assert.*;

public class GetPlayerControllerTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(GetPlayerControllerTest.class);
    private static final String SCHEMA_PATH = "schemas/get_all_players_schema.json";
    private final ThreadLocal<Long> createdPlayerId = new ThreadLocal<>();

    @Test
    @Step("Get player by ID - positive test")
    @Issue("JIRA-1234")
    @Description("Issue with player creation")
    public void testGetPlayerByIdWithValidData() {
        reportMessage(logger, "Create test player");
        PlayerCreateRequestDto playerCreateRequestDto = generateValidPlayer();
        PlayerCreateResponseDto createdPlayer = playerApi.get().createPlayer(ADMIN,
                playerCreateRequestDto).then().extract().as(PlayerCreateResponseDto.class);
        logger.debug("Created test player: " + createdPlayer);

        Response response = playerApi.get().getPlayerById(createdPlayer.getId());

        reportMessage(logger,"Validate response against JSON schema");
        response.then().statusCode(SC_OK).body(JsonSchemaValidator.matchesJsonSchemaInClasspath(SCHEMA_PATH));

        reportMessage(logger, "Compare the response values with expected data");
        PlayerCreateResponseDto playerCreateResponseDto = response.then().extract().as(PlayerCreateResponseDto.class);

        assertTrue(playerCreateResponseDto.matchesPlayer(playerCreateRequestDto), "Created player should match the input data");

        createdPlayerId.set(playerCreateResponseDto.getId());
    }

    @Test
    @Step("Get player by ID - negative test")
    public void testGetPlayerByInvalidId() {
        reportMessage(logger, "Get player by incorrect ID format");
        Response response = playerApi.get().getPlayerByIncorrectIdFormat(UUID.randomUUID().toString());

        reportMessage(logger,"Get operation should not be successful for wrong ID format data");
        response.then().statusCode(SC_BAD_REQUEST);
    }

    @Test
    @Step("Get all players - test is failed due to the issue with Player creation")
    @Issue("JIRA-1234")
    @Description("Issue with player creation")
    public void testNewlyCreatedPlayerContainsInGetAllPlayers() {
        reportMessage(logger, "Create new test player");
        PlayerCreateRequestDto playerCreateRequestDto = generateValidPlayer();
        //issue is here, we can not create new Player. 403 SC and no response body
        PlayerCreateResponseDto createdPlayer = playerApi.get().createPlayer(SUPERVISOR, playerCreateRequestDto)
                .then().extract().as(PlayerCreateResponseDto.class);
        logger.debug("Created test player: " + createdPlayer);

        Response response = playerApi.get().getAllPlayers();
        reportMessage(logger, "Check the status code");
        response.then().statusCode(SC_OK);

        PlayerGetAllResponseDto allPlayersResponse = response.as(PlayerGetAllResponseDto.class);
        reportMessage(logger, "All players response: " + allPlayersResponse);
        assertNotNull(allPlayersResponse.getPlayers(), "Players list should not be null");
        assertTrue(allPlayersResponse.getPlayers().size() > 0, "Players list should not be empty");

        // Find and validate the test player. This step is failed due to issue with player creation
        Optional<PlayerItemDto> foundPlayer = findTestPlayer(allPlayersResponse, playerCreateRequestDto);
        assertTrue(foundPlayer.isPresent(), "Test player should be found in the response");
        assertTrue(foundPlayer.get().matchesPlayer(playerCreateRequestDto), "Found player should match test player");

        createdPlayerId.set(createdPlayer.getId());
    }

    @Test
    @Step("Get all players. Test is failed due to missed 'role' field")
    @Issue("JIRA-123")
    @Description("Issue with missed 'role' field which is required")
    public void testGetAllPlayers() {
        Response response = playerApi.get().getAllPlayers();

        //bug is here
        reportMessage(logger, "Validate response against JSON schema: " + response.asPrettyString());
        response.then().statusCode(SC_OK).body(JsonSchemaValidator.matchesJsonSchemaInClasspath(SCHEMA_PATH));

        PlayerGetAllResponseDto allPlayersResponse = response.as(PlayerGetAllResponseDto.class);
        reportMessage(logger, "All players response: " + allPlayersResponse);
        assertNotNull(allPlayersResponse.getPlayers(), "Players list should not be null");
        assertTrue(allPlayersResponse.getPlayers().size() > 0, "Players list should not be empty");
    }

    private Optional<PlayerItemDto> findTestPlayer(PlayerGetAllResponseDto response, PlayerCreateRequestDto testPlayer) {
        return response.getPlayers().stream()
                .filter(player -> player.getScreenName().equals(testPlayer.getScreenName()))
                .findFirst();
    }

    @AfterMethod
    @Step("Clean up test data")
    public void cleanUp() {
        if (nonNull(createdPlayerId.get())) {
            long playerId = createdPlayerId.get();
            reportMessage(logger, "Cleaning up created player with ID: " + playerId);
            try {
                playerApi.get().deletePlayer(ADMIN, playerId);
                reportMessage(logger, "Successfully deleted player with ID: " + playerId);
            } catch (Exception e) {
                logger.error("Failed to delete player with ID: {}", playerId, e);
            } finally {
                createdPlayerId.remove();
            }
        }
    }
}