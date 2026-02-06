package com.assetmanager.api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseApiTest {

    @LocalServerPort
    protected int port;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/assets";
        // This ensures every test starts with a clean "connection" to the server
    }
}