package com.assetmanager.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest
    @CsvSource({
        "laptop, , Inspire, brand: Brand is mandatory", // Missing brand laptop
        "laptop, Dell, , model: Model is mandatory",   // Missing model laptop
        "phone, , iPhone 15, brand: Brand is mandatory", // Missing brand phone
        "phone, Apple, , model: Model is mandatory"    // Missing model phone
    })
    public void shouldReturnBadRequestForMissingFields(String type, String brand, String model, String expectedMessage) {
        String payload = """
            {
                "type": "%s",
                "deviceId": "TEST-PARAM-01",
                "brand": "%s",
                "model": "%s"
            }
        """.formatted(type, brand == null ? "" : brand, model == null ? "" : model);

        given()
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post()
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("message", containsString(expectedMessage));
    }

    @Test
    public void shouldReturnBadRequestForInvalidRamSize() {
        String invalidRamJson = """
            {
                "type": "laptop",
                "deviceId": "TEST-RAM-01",
                "brand": "TestBrand",
                "model": "TestModel",
                "operatingSystem": "Windows 11",
                "ramSizeGb": -4
            }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(invalidRamJson)
        .when()
            .post()
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("message", containsString("ramSizeGb: RAM size must be a positive integer."));
    }   

    @ParameterizedTest
    @CsvSource({
        "laptop, 01, TestBrand, TestModel, Windows 11, 16", // Short deviceId laptop
        "laptop, laptoptestbrandtesmodelwindows11, TestBrand , TestModel, Windows 10, 8 "   // Large deviceId laptop
    })  
    public void shouldReturnBadRequestForInvalidLaptopDeviceIdLength(String type, String deviceId, String brand, String model, String operatingSystem, int ramSizeGb) {
        String invalidDeviceIdJson = """
            {
                "type": "%s",
                "deviceId": "%s",
                "brand": "%s",
                "model": "%s",
                "operatingSystem": "%s",
                "ramSizeGb": %d
            }
        """.formatted(type, deviceId, brand, model, operatingSystem, ramSizeGb);

        given()
            .contentType(ContentType.JSON)
            .body(invalidDeviceIdJson)
        .when()
            .post()
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("message", containsString("deviceId: Device ID must be between 3 and 20 characters"));
    }

    @ParameterizedTest
    @CsvSource({
        "phone, 02, iPhone 15, TestModel, MacOS, 3145678465", // Short deviceId
        "phone, phonetestbrandtestmodelmacos, Apple, TestModel, MacOS, 3145678478" // Large deviceId
    })  
    public void shouldReturnBadRequestForInvalidPhoneDeviceIdLength(String type, String deviceId, String brand, String model, String operatingSystem, String phoneNumber) {
        String invalidDeviceIdJson = """
            {
                "type": "%s",
                "deviceId": "%s",
                "brand": "%s",
                "model": "%s",
                "operatingSystem": "%s",
                "phoneNumber": "%s"
            }
        """.formatted(type, deviceId, brand, model, operatingSystem, phoneNumber);

        given()
            .contentType(ContentType.JSON)
            .body(invalidDeviceIdJson)
        .when()
            .post()
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"))
            .body("message", containsString("deviceId: Device ID must be between 3 and 20 characters"));
    }
}