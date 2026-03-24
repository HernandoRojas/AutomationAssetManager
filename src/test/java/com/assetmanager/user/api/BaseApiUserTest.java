package com.assetmanager.user.api;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import com.assetmanager.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseApiUserTest {
    
    @LocalServerPort
    protected int port;

    @Autowired
    protected UserRepository userRepository; // Inject the UserRepository to verify user-related operations


    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/users";
        // This ensures every test starts with a clean "connection" to the server

        // Ensure Database is clean before EVERY test
        userRepository.deleteAll();
    }
}
