package com.assetmanager.api;

import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;

import com.assetmanager.model.Device;
import com.assetmanager.model.DeviceStatus; 

import com.assetmanager.model.MobilePhone;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;


public class DevicePersistenceTest extends BaseApiTest {

    @Test
    public void testPostDevicePersistsInDatabase() {
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

        // 1. Act: Call the API
        io.restassured.RestAssured.given()
            .contentType(ContentType.JSON)
            .body(deviceJson)
        .when()
            .post()
        .then()
            .statusCode(201);

        // 2. Assert: Verify the data is REALLY in the database
        Optional<Device> savedDevice = repository.findById("PH-99");
        
        assertTrue(savedDevice.isPresent(), "Device should be found in the database");
        assertEquals(DeviceStatus.AVAILABLE, savedDevice.get().getStatus());
        assertEquals("Apple", savedDevice.get().getBrand());
        
        // Check if it's specifically a MobilePhone in the database
        assertTrue(savedDevice.get() instanceof MobilePhone);
    }
}