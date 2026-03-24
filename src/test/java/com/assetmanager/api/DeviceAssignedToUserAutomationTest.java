package com.assetmanager.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;

public class DeviceAssignedToUserAutomationTest extends BaseApiTest {
    // This class is intended to test the scenario where a device is assigned to a user.
    // The actual implementation of the test will depend on the API endpoints and business logic related to device management.


    @Test
    public void testDeviceAssignedToUser() {
        // Implement the test logic for assigning a device to a user and verifying the expected behavior.
        // This may involve creating a user, creating a device, assigning the device to the user, and then verifying the assignment.

        //data for user
        int userId = 123; // Example user ID for testing
        String username = "testuser"; // Example username for testing
        String employeeId = "234255"; // Example employee ID for testing

        // data for device
        String deviceId = "M001"; // Example device ID for testing
        String type = "phone"; // Example device name for testing
        String brand = "Apple"; // Example device brand for testing
        String model = "iPhone 13"; // Example device model for testing
        String operatingSystem = "iOS"; // Example device operating system for testing
        String phoneNumber = "123-456-7890"; // Example device phone number for testing

        String basePathUser = "/api/users";

        String userJson = """
            {
                "userId": %d,
                "username": "%s",
                "employeeId" : "%s"
            }
            """.formatted(userId, username, employeeId);

        String deviceJson = """
            {
                "deviceId": "%s",
                "type": "%s",
                "brand": "%s",
                "model": "%s",
                "operatingSystem": "%s",
                "phoneNumber": "%s"
            }
            """.formatted(deviceId, type, brand, model, operatingSystem, phoneNumber);

        // Register a user to rent the device
        given()
            .basePath(basePathUser)
            .contentType(ContentType.JSON)
            .body(userJson)    
        .when()
            .post()
        .then()
            .statusCode(201);

        // Create a device
        given()
            .contentType(ContentType.JSON)
            .body(deviceJson)
        .when()
            .post()
        .then()
            .statusCode(201);

        // Rent the device to the user
        given()
            .pathParam("id", deviceId)
            .pathParam("userId", userId)
        .when()
            .post("/{id}/rent/{userId}")
        .then()
            .statusCode(200)
            .body("deviceId", equalTo(deviceId))
            .body("owner.userId", equalTo(userId));

        // Get Devices assigned to the user and verify the device is linked
        given()
            .pathParam("employeeId", employeeId)
        .when()
            .get("/user/{employeeId}")
        .then()
            .statusCode(200)
            .body("[0].deviceId", equalTo(deviceId))
            .body("[0].owner.userId", equalTo(userId));
    }
}