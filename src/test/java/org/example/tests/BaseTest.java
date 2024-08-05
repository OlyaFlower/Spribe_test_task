package org.example.tests;

import io.qameta.allure.Allure;
import org.example.api.PlayerApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    protected static final ThreadLocal<PlayerApi> playerApi = new ThreadLocal<>();
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    private void initializePlayerApi() {
        if (playerApi.get() == null) {
            logger.info("Initializing playerApi for thread: " + Thread.currentThread().getId());
            playerApi.set(new PlayerApi());
        } else {
            logger.info("PlayerApi already initialized for thread: " + Thread.currentThread().getId());
        }
    }

    @BeforeMethod
    public void setUp() {
        logger.info("BaseTest setUp method started for thread: " + Thread.currentThread().getId());
        initializePlayerApi();
    }

    @AfterMethod
    public void tearDown() {
        logger.info("BaseTest tearDown method started");
        playerApi.remove(); // Clean up the ThreadLocal after each test
    }

    public void reportMessage(Logger logger, String message) {
        logger.info(message);
        Allure.step(message);
    }

    public enum Editor{
        SUPERVISOR("supervisor"),
        ADMIN("admin");

        String value;
        private Editor(String value) {
            this.value = value;
        }

        public String getValue() {return value;}
    }
}