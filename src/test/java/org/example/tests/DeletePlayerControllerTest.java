package org.example.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.Step;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.example.model.PlayerCreateRequestDto;
import org.example.model.PlayerCreateResponseDto;
import org.example.util.TestDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.apache.http.HttpStatus.*;
import static org.example.tests.BaseTest.Editor.ADMIN;
import static org.example.tests.BaseTest.Editor.SUPERVISOR;

public class DeletePlayerControllerTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(DeletePlayerControllerTest.class);
    private static final String SCHEMA_PATH = "schemas/delete_player_response_schema.json";

    @Step("Create test player")
    private PlayerCreateResponseDto createTestPlayer() {
        PlayerCreateRequestDto player = TestDataGenerator.generateValidPlayer();
        PlayerCreateResponseDto response = playerApi.get().createPlayer(SUPERVISOR, player).then().extract().as(PlayerCreateResponseDto.class);;
        reportMessage(logger,"Created test player with ID: " + response.getId());
        return response;
    }

    @Test
    @Step("Delete player - positive test")
    @Issue("JIRA-1234")
    @Description("Issue with player creation")
    public void testDeletePlayerWithValidId() {
        Long playerId = createTestPlayer().getId();
        Response response = playerApi.get().deletePlayer(SUPERVISOR, playerId);

        reportMessage(logger,"Validate response against JSON schema");
        //issue is here, we can not create new Player and as a result we can't delete it. 403 SC and no response body
        response.then().statusCode(SC_NOT_FOUND).body(JsonSchemaValidator.matchesJsonSchemaInClasspath(SCHEMA_PATH));

        reportMessage(logger,"Verify the player is deleted");
        response = playerApi.get().getPlayerById(playerId);
        response.then().statusCode(SC_NOT_FOUND);
    }

    @Test
    @Step("Delete player - negative test (non-existent player)")
    @Issue("JIRA-1235")
    @Description("Incorrect status code is received")
    public void testDeletePlayerWithInvalidId() {
        long nonExistentPlayerId = 999999L;
        Response response = playerApi.get().deletePlayer(ADMIN, nonExistentPlayerId);

        reportMessage(logger,"Delete operation should not be successful for non-existent player");
        //issue is here, even with wrong Player id parameter we got 403 SC instead of 400 Bad Request
        response.then().statusCode(SC_BAD_REQUEST);
    }
}