package com.assetmanager.api;

import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class FilteringAutomationTest extends BaseApiTest {
    
    @Test
    public void shouldFilterDevicesByStatusAndBrand() {
        // 1. Arrange: Register multiple devices with different statuses and brands

        // Creating the first phone device (Apple, AVAILABLE)
        String phoneJson = """
            {
                "type": "phone",
                "deviceId": "M001",
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
            .body("deviceId", equalTo("M001"));

        // Creating the second phone device (Samsung, AVAILABLE)
        String phoneJson2 = """
            {
                "type": "phone",
                "deviceId": "M002",
                "brand": "Samsung",
                "model": "Galaxy S23",
                "operatingSystem": "Android 13",
                "phoneNumber": "3124567890"
            }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(phoneJson2)
        .when()
            .post()
        .then()
            .statusCode(201)
            .body("status", equalTo("AVAILABLE"))
            .body("deviceId", equalTo("M002"));

        // Creating the third phone device (Apple, AVAILABLE)
        String phoneJson3 = """
            {
                "type": "phone",
                "deviceId": "M003",
                "brand": "Apple",
                "model": "iPhone 14",
                "operatingSystem": "iOS 16",
                "phoneNumber": "3124567891"
            }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(phoneJson3)
        .when()
            .post()
        .then()
            .statusCode(201)
            .body("status", equalTo("AVAILABLE"))
            .body("deviceId", equalTo("M003"));

        // Renting the second device to change its status to IN_USE
        given()
            .pathParam("id", "M002")
        .when()
            .post("/{id}/rent")
        .then()
            .statusCode(200)
            .body("status", equalTo("IN_USE"))
            .body("deviceId", equalTo("M002"));

        // 2. Act: Execute the GET request with filtering parameters
        // For example, filter by status=AVAILABLE and brand=Apple
        given()
            .queryParam("status", "AVAILABLE")
            .queryParam("brand", "Apple")
        .when()
            .get()
        // 3. Assert: Verify that the response contains only devices that match the criteria
        .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("[0].deviceId", equalTo("M003"))
            .body("[0].status", equalTo("AVAILABLE"))
            .body("[0].brand", equalTo("Apple"))
            .body("[1].deviceId", equalTo("M001"))
            .body("[1].status", equalTo("AVAILABLE"))
            .body("[1].brand", equalTo("Apple"));

            // 2. Act: Execute the GET request with filtering parameters
        // For example, filter by status=IN_USE and brand=Samsung
        given()
            .queryParam("status", "IN_USE")
            .queryParam("brand", "Samsung")
        .when()
            .get()
        // 3. Assert: Verify that the response contains only devices that match the criteria
        .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].deviceId", equalTo("M002"))
            .body("[0].status", equalTo("IN_USE"))
            .body("[0].brand", equalTo("Samsung"));
    }
    
}
