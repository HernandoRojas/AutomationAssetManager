package com.assetmanager.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class DecommissionAutomationTest extends BaseApiTest {

    @Test
    public void shouldDecommissionPhoneSuccessfully() {
        // 1. Arrange: Register a new phone device to ensure it exists before decommissioning
        String phoneJson = """
            {
                "type": "phone",
                "deviceId": "TEST-PH-01",
                "brand": "Apple",
                "model": "iPhone 15",
                "operatingSystem": "iOS 17",
                "phoneNumber": "3124567654"
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
            .body("deviceId", equalTo("TEST-PH-01"));

        // 2. Act: Execute the Decommission PATCH
        given()
            .pathParam("id", "TEST-PH-01")
        .when()
            .patch("/{id}/decommission")
        // 3. Assert: Verify US-005 Acceptance Criteria
        .then()
            .statusCode(200)
            .body("status", equalTo("DECOMMISSIONED"))
            .body("decommissionDate", notNullValue())
            .body("brand", equalTo("Apple"));
    }

    @Test
    public void shouldReturnConflictWhenDecommissioningInUseDevice() {
    // 1. Create and Rent a device (Assuming you have a /rent endpoint)
    // Or simply use a device that is already IN_USE
        String phoneJson = """
            {
                "type": "phone",
                "deviceId": "TEST-PH-02",
                "brand": "Apple",
                "model": "iPhone 16",
                "operatingSystem": "iOS 17",
                "phoneNumber": "3124897654"
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
            .body("deviceId", equalTo("TEST-PH-02"));

        // Rent the device to set its status to IN_USE
        given()
            .pathParam("id", "TEST-PH-02")
        .when()
            .post("/{id}/rent")
        .then()
            .statusCode(200)
            .body("status", equalTo("IN_USE"))
            .body("deviceId", equalTo("TEST-PH-02"))
            .body("brand", equalTo("Apple"))
            .body("model", equalTo("iPhone 16"))
            .body("operatingSystem", equalTo("iOS 17"))
            .body("phoneNumber", equalTo("3124897654"));

        // 2. Attempt to Decommission the IN_USE device
        given()
            .pathParam("id", "TEST-PH-02")
        .when()
            .patch("/{id}/decommission")
        .then()
            .statusCode(409) // Our standardized HTTP response
            .body("error", equalTo("Business Rule Violation"));
}
    
}
