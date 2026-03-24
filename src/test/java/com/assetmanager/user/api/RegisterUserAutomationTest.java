package com.assetmanager.user.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.assetmanager.model.User;

public class RegisterUserAutomationTest extends BaseApiUserTest {

    // Test case for successful user registration
    @Test
    public void testRegisterUser_Success() {

        int userId = 123; // Example user ID for testing
        String username = "testuser"; // Example username for testing
        String employeeId = "234255"; // Example employee ID for testing
        String userJson = """
            {
                "userId": %d,
                "username": "%s",
                "employeeId" : "%s"
            }
            """.formatted(userId, username, employeeId);

        // Implement the test logic for successful user registration

        given()
            .contentType("application/json")
            .body(userJson)
        .when()
            .post() 
        .then()
            .statusCode(201) // Expecting HTTP 201 Created for successful registration
            .body("userId", equalTo(userId))
            .body("username", equalTo(username))
            .body("employeeId", equalTo(employeeId));

            // 2. Assert: Verify the data is REALLY in the database
        List<User> savedUser = userRepository.findByUserId(userId);
        
        assertTrue(!savedUser.isEmpty(), "User should be found in the database");
        assertEquals(userId, savedUser.get(0).getUserId());
        assertEquals(username, savedUser.get(0).getUsername());
        assertEquals(employeeId, savedUser.get(0).getEmployeeId());
        
    }
}
