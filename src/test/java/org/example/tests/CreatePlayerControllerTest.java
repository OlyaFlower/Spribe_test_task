package org.example.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.Step;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.example.model.PlayerCreateRequestDto;
import org.example.model.PlayerCreateResponseDto;
import org.example.util.TestDataGenerator;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.example.tests.BaseTest.Editor.ADMIN;
import static org.example.tests.BaseTest.Editor.SUPERVISOR;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class CreatePlayerControllerTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CreatePlayerControllerTest.class);
    private static final String SCHEMA_PATH = "schemas/player_schema.json";
    private final ThreadLocal<Long> createdPlayerId = new ThreadLocal<>();

    @BeforeMethod
    public void setUp() {
        super.setUp();
    }

    @Test
    @Issue("JIRA-1234")
    @Description("Issue with Player creation")
    public void testCreatePlayerByAdminRoleWithValidData() {
        PlayerCreateRequestDto playerCreateRequestDto = TestDataGenerator.generateValidPlayer();
        logger.debug("Generated valid player: {}", playerCreateRequestDto);

        reportMessage(logger, "Check the status code and validate the json response by schema");
        Response response = playerApi.get().createPlayer(ADMIN, playerCreateRequestDto);
        //issue is here, we can not create new Player. 403 SC and no response body
        response.then().statusCode(SC_OK).body(JsonSchemaValidator.matchesJsonSchemaInClasspath(SCHEMA_PATH));

        reportMessage(logger, "Compare the response values with expected data");
        PlayerCreateResponseDto playerCreateResponseDto = response.then().extract().as(PlayerCreateResponseDto.class);

        assertTrue(playerCreateResponseDto.matchesPlayer(playerCreateRequestDto), "Created player should match the input data");
        assertNotNull(playerCreateResponseDto.getId(), "Player ID should not be null");

        createdPlayerId.set(playerCreateResponseDto.getId());
        logger.debug("Save the created player ID: {}", createdPlayerId);
    }

    @Test
    @Issue("JIRA-1234")
    @Description("Issue with player creation")
    public void testCreatePlayerBySupervisorRoleWithInvalidData() {
        PlayerCreateRequestDto player = TestDataGenerator.generateInvalidPlayer();
        logger.debug("Generated player with invalid age: {}", player);

        reportMessage(logger, "Check the status code of invalid request.");
        Response response = playerApi.get().createPlayer(SUPERVISOR, player);
        //issue is here, even with invalid parameter we got 403 SC
        response.then().statusCode(SC_BAD_REQUEST);
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