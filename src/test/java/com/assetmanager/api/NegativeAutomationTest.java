package com.assetmanager.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class NegativeAutomationTest extends BaseApiTest {
    // This class will contain tests that intentionally trigger error conditions
    // to verify that the API responds with appropriate error messages and status codes.

    @Test
    public void shouldReturnBadRequestForInvalidDeviceType() {
        String invalidDeviceJson = """
            {
                "type": "invalid_device",
                "deviceId": "TEST-INV-01",
                "brand": "Unknown",
                "model": "Unknown Model",
                "operatingSystem": "Unknown OS",
                "ramSizeGb": 1
            }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(invalidDeviceJson)
        .when()
            .post()
        .then()
            .statusCode(400)
            .body("error", equalTo("Bad Request"));
    }
    
    @Test
    public void shouldReturnNotFoundForNonExistentDevice() {
        String nonExistentDeviceId = "NON-EXISTENT-123";

        given()
            .pathParam("id", nonExistentDeviceId)
        .when()
            .post("/{id}/rent")
        .then()
            .statusCode(404)
            .body("error", equalTo("Device Not Found"));
    }

    @Test
    public void shouldReturnRuleViolationWhenRentingAlreadyRentedDevice() {
        String deviceId = "TEST-RENT-01";

        // First, register a new device
        String newDeviceJson = """
            {
                "type": "laptop",
                "deviceId": "%s",
                "brand": "TestBrand",
                "model": "TestModel",
                "operatingSystem": "Windows 11",
                "ramSizeGb": 16
            }
        """.formatted(deviceId);

        given()
            .contentType(ContentType.JSON)
            .body(newDeviceJson)
        .when()
            .post()
        .then()
            .statusCode(201);

        // Rent the device for the first time
        given()
            .pathParam("id", deviceId)
        .when()
            .post("/{id}/rent")
        .then()
            .statusCode(200);

        // Attempt to rent the same device again, which should fail
        given()
            .pathParam("id", deviceId)
        .when()
            .post("/{id}/rent")
        .then()
            .statusCode(409)
            .body("error", equalTo("Business Rule Violation"));
    }

    @Test
    public void shouldReturnBadRequestForMissingBrandFieldOnLaptop() {
        String incompleteDeviceJson = """
            {
                "type": "laptop",
                "deviceId": "TEST-MISSING-FIELDS-01",
                "model": "Inspire",
                "operatingSystem": "Windows 11",
                "ramSizeGb": 16
            }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(incompleteDeviceJson)
        .when()
            .post()
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("message", containsString("brand: Brand is mandatory"));

    }

    @Test
    public void shouldReturnBadRequestForMissingModelFieldOnLaptop() {
        String incompleteDeviceJson = """
            {
                "type": "laptop",
                "deviceId": "TEST-MISSING-FIELDS-02",
                "brand": "TestBrand",
                "operatingSystem": "Windows 11",
                "ramSizeGb": 16
            }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(incompleteDeviceJson)
        .when()
            .post()
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("message", containsString("model: Model is mandatory"));
    }

    @Test
    public void shouldReturnBadRequestForMissingBrandFieldOnPhone() {
        String incompleteDeviceJson = """
            {
                "type": "phone",
                "deviceId": "TEST-MISSING-BRAND-PHONE-01",
                "model": "TestModel",
                "operatingSystem": "iOS 16",
                "phoneNumber": "3124567891"
            }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(incompleteDeviceJson)
        .when()
            .post()
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("message", containsString("brand: Brand is mandatory"));
    }

    @Test
    public void shouldReturnBadRequestForMissingModelFieldOnPhone() {
        String incompleteDeviceJson = """
            {
                "type": "phone",
                "deviceId": "TEST-MISSING-MODEL-PHONE-01",
                "brand": "TestBrand",
                "operatingSystem": "iOS 16",
                "phoneNumber": "3124567891"
            }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(incompleteDeviceJson)
        .when()
            .post()
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("message", containsString("model: Model is mandatory"));
    }  

}