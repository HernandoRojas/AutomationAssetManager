package com.assetmanager.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class DeallocationAndTransferAutomationTest extends BaseApiTest {

    String basePathUser = "/api/users";

    @Test
    public void shouldDeallocateDeviceSuccessfully() {
        // 1. Arrange: Register a new phone device and rent it to ensure it's IN_USE before deallocation
        String phoneJson = """
            {
                "type": "phone",
                "deviceId": "TEST-PH-03",
                "brand": "Samsung",
                "model": "Galaxy S21",
                "operatingSystem": "Android 11",
                "phoneNumber": "3124567890"
            }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(phoneJson)
        .when()
            .post()
        .then()
            .statusCode(201)
            .body("status", equalTo("AVAILABLE"))
            .body("deviceId", equalTo("TEST-PH-03"));

            String userJson = """
                {
                    "userId": 1,
                    "username": "Test User",
                    "employeeId": "235425"
                }
            """;
        // Register a user to rent the device
        given()
            .basePath(basePathUser)
            .contentType(ContentType.JSON)
            .body(userJson)
        .when()
            .post()
        .then()
            .statusCode(201);


        // Rent the device to change its status to IN_USE
        given()
            .pathParam("id", "TEST-PH-03")
            .pathParam("userId", 1)
        .when()
            .post("/{id}/rent/{userId}")
        .then()
            .statusCode(200)
            .body("status", equalTo("IN_USE"));

        // 2. Act: Execute the Deallocation PATCH to return the device
        given()
            .pathParam("id", "TEST-PH-03")
        .when()
            .post("/{id}/return")
        // 3. Assert: Verify that deallocation was successful by checking that the device status is back to AVAILABLE and the owner is set to null.
        .then()
            .statusCode(200)
            .body("status", equalTo("AVAILABLE"))
            .body("brand", equalTo("Samsung"))
            .body("user", nullValue());
    }

    @Test
    public void shouldTransferDeviceSuccessfully() {
        // 1. Arrange: Register a new device and two users to transfer between
        String deviceJson = """
            {
                "type": "laptop",
                "deviceId": "TEST-LT-01",
                "brand": "Dell",
                "model": "XPS 13",
                "operatingSystem": "Windows 10",
                "ramSizeGb": 16
            }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(deviceJson)
        .when()
            .post()
        .then()
            .statusCode(201)
            .body("status", equalTo("AVAILABLE"))
            .body("deviceId", equalTo("TEST-LT-01"));

        String user1Json = """
            {
                "userId": 1,
                "username": "User One",
                "employeeId": "EMP001"
            }
        """;

        String user2Json = """
            {
                "userId": 2,
                "username": "User Two",
                "employeeId": "EMP002"
            }
        """;

        // Register both users
        given()
            .basePath(basePathUser)
            .contentType(ContentType.JSON)
            .body(user1Json)
        .when()
            .post()
        .then()
            .statusCode(201);

        given()
            .basePath(basePathUser)
            .contentType(ContentType.JSON)
            .body(user2Json)
        .when()
            .post()
        .then()
            .statusCode(201);

        // Rent the device to the first user to set up the transfer scenario
        given()
            .pathParam("id", "TEST-LT-01")
            .pathParam("userId", 1)
        .when()
            .post("/{id}/rent/{userId}")
        .then()
            .statusCode(200)
            .body("status", equalTo("IN_USE"))
            .body("owner.employeeId", equalTo("EMP001"));

        // 2. Act: Execute the Transfer PATCH to transfer from User One to User Two
        given()
            .pathParam("deviceId", "TEST-LT-01")
            .pathParam("targetEmployeeId", "EMP002")
        .when()
            .patch("/{deviceId}/transfer/{targetEmployeeId}")
        .then()
            .statusCode(200)
            .body("status", equalTo("IN_USE"))
            .body("owner.employeeId", equalTo("EMP002"));
    }

    @Test
    public void shouldReturnNotFoundWhenTransferringDeviceToNonExistentUser() {
        // 1. Arrange: Register a new device to transfer and a user to ensure the device is in a valid state for transfer
        String deviceJson = """
            {
                "type": "laptop",
                "deviceId": "TEST-LT-02",
                "brand": "HP",
                "model": "Spectre x360",
                "operatingSystem": "Windows 10",
                "ramSizeGb": 16
            }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(deviceJson)
        .when()
            .post()
        .then()
            .statusCode(201)
            .body("status", equalTo("AVAILABLE"))
            .body("deviceId", equalTo("TEST-LT-02"));

        String user1Json = """
            {
                "userId": 1,
                "username": "User One",
                "employeeId": "EMP001"
            }
        """;
        // Register the user
        given()
            .basePath(basePathUser)
            .contentType(ContentType.JSON)
            .body(user1Json)
        .when()
            .post()
        .then()
            .statusCode(201);
        // Rent the device to the user to set up the transfer scenario
        given()
            .pathParam("id", "TEST-LT-02")
            .pathParam("userId", 1)
        .when()
            .post("/{id}/rent/{userId}")
        .then()
            .statusCode(200);


        // 2. Act: Attempt to transfer the device to a non-existent user (e.g., employeeId "EMP999")
        given()
            .pathParam("deviceId", "TEST-LT-02")
            .pathParam("targetEmployeeId", "EMP999")
        .when()
            .patch("/{deviceId}/transfer/{targetEmployeeId}")
        // 3. Assert: Verify that the response status is 404 Not Found and contains an appropriate error message.
        .then()
            .statusCode(404)
            .body("message", containsString("User with ID 0 was not found."))
            .body("error", equalTo("User Not Found"));
    }
}