package org.example.util;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.example.config.Configuration;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ApiClient {
    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    static {
        RestAssured.baseURI = Configuration.getBaseUrl();
    }

    @Step("Send GET request to {path}")
    public static Response get(String path) {
        logger.info("Sending GET request to {}", path);
        Response response = given().get(path);
        logResponse(response);
        return response;
    }

    @Step("Send GET request to {path} with query params")
    public static Response get(String path, Map<String, Object> queryParams) {
        logger.info("Sending GET request to {} with query params: {}", path, queryParams);
        Response response = given().queryParams(queryParams).get(path);
        logResponse(response);
        return response;
    }

    @Step("Send POST request to {path}")
    public static Response post(String path, Object body) {
        logger.info("Sending POST request to {} with body: {}", path, body);
        Response response = given().body(body).post(path);
        logResponse(response);
        return response;
    }

    @Step("Send DELETE request to {path}")
    public static Response delete(String path, Object body) {
        logger.info("Sending DELETE request to {} with body: {}", path, body);
        Response response = given().body(body).delete(path);
        logResponse(response);
        return response;
    }

    @Step("Send PATCH request to {path}")
    public static Response patch(String path, Object body) {
        logger.info("Sending PATCH request to {} with body: {}", path, body);
        Response response = given().body(body).patch(path);
        logResponse(response);
        return response;
    }


    private static RequestSpecification given() {
        return RestAssured.given()
                .contentType("application/json")
                .accept("application/json");
    }

    @Step("Log response details")
    private static void logResponse(Response response) {
        logger.info("Response Status Code: {}", response.getStatusCode());
        Allure.step("Response SC: " + response.getStatusCode());

        logger.debug("Response Headers: {}", response.getHeaders());
        Allure.step("Response headers: " + response.getHeaders());

        logger.debug("Response Body: {}", response.getBody().asString());
        Allure.step("Response Body: " + response.getBody().asPrettyString());
    }
}