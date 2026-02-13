package com.assetmanager.api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.assetmanager.repository.DeviceRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseApiTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected DeviceRepository repository; // Inject the repository to verify the DB


    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/assets";
        // This ensures every test starts with a clean "connection" to the server

        // Ensure Database is clean before EVERY test
        repository.deleteAll();
    }
}