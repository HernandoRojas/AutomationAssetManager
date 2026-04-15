package com.assetmanager.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*; 

public class BatchRegisterDevicesTest extends BaseApiTest{

    @Test
    public void shouldBatchRegisterDevicesSuccessfully() {
        // 1. Arrange: Create a JSON array of devices to register
        String devicesJson = """
            {
                "devices": [      
                {
                    "type": "laptop",
                    "deviceId": "TEST-LT-01",
                    "brand": "Dell",
                    "model": "XPS 13",
                    "operatingSystem": "Windows 10",
                    "ramSizeGb": 16
                },
                {
                    "type": "phone",
                    "deviceId": "TEST-PH-01",
                    "brand": "Apple",
                    "model": "iPhone 12",
                    "operatingSystem": "iOS 14",
                    "phoneNumber": "1234567890"
                },
                {
                    "type": "phone",
                    "deviceId": "TEST-PH-02",
                    "brand": "Samsung",
                    "model": "Galaxy Tab S7",
                    "operatingSystem": "Android 11",
                    "phoneNumber": "0987654321"
                }
                ]
        }
        """;

        // 2. Act & Assert: Send POST request to batch register devices and verify response
        given()
            .contentType(ContentType.JSON)
            .body(devicesJson)
        .when()
            .post("/batch")
        .then()
            .statusCode(201)
            .body("message", is("Batch registration completed successfully"));
    }

    @Test
    public void shouldReturnBadRequestForInvalidBatchRegistration() {
        // 1. Arrange: Create a JSON array with an null device entry to simulate invalid input
        String invalidDeviceJson = """
            {
                "devices": [
                {
                     "type": "laptop",
                    "deviceId": "TEST-LT-01",
                    "brand": "Dell",
                    "model": "XPS 13",
                    "operatingSystem": "Windows 10",
                    "ramSizeGb": 16
                },
                {
                    "type": "phone",
                    "brand": "Apple",
                    "model": "iPhone 12",
                    "operatingSystem": "iOS 14",
                    "phoneNumber": "1234567890"
                }
                ]
            }
        """;

        // 2. Act & Assert: Send POST request and expect a 400 Bad Request response
        given()
            .contentType(ContentType.JSON)
            .body(invalidDeviceJson)
        .when()
            .post("/batch")
        .then()
            .statusCode(400)
            .body("error", is("Validation Failed"))
            .body("message", containsString("deviceId: Device ID cannot be null"));
    }

    @Test
    public void shouldReturnBadRequestForDuplicateDeviceIds() {
        // 1. Arrange: Register a device with a specific ID, then attempt to register another device with the same ID in a batch request
        String deviceJson = """
            {
                "type": "phone",
                "deviceId": "PH-99",
                "brand": "Apple",
                "model": "iPhone 15",
                "operatingSystem": "iOS",
                "phoneNumber": "555-0101"
            }
        """;

        // Call the API
        given()
            .contentType(ContentType.JSON)
            .body(deviceJson)
        .when()
            .post()
        .then()
            .statusCode(201);
        

        String duplicateDeviceJson = """
            {
                "devices": [
                {
                    "type": "laptop",
                    "deviceId": "TEST-LT-01",
                    "brand": "Dell",
                    "model": "XPS 13",
                    "operatingSystem": "Windows 10",
                    "ramSizeGb": 16
                },
                {
                    "type": "phone",
                    "deviceId": "PH-99", 
                    "brand": "Apple",
                    "model": "iPhone 12",
                    "operatingSystem": "iOS 14",
                    "phoneNumber": "1234567890"
                }
                ]
            }
        """;

        // 2. Act & Assert: Send POST request and expect a 400 Bad Request response due to duplicate device IDs
        given()
            .contentType(ContentType.JSON)
            .body(duplicateDeviceJson)
        .when()
            .post("/batch")
        .then()
            .statusCode(400)
            .body("error", is("Invalid Input"))
            .body("message", is("Batch processing failed at index 1: Device ID already exists: PH-99"));
    }
}