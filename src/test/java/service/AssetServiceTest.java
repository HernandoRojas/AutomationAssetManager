package service;

// Standard JUnit 5 Imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.DeviceNotFoundException;
import repository.InMemoryDeviceRepository;
import exception.InvalidDeviceStateException;
import model.MobilePhone;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;


class AssetServiceTest {

    private AssetService assetService;
    private InMemoryDeviceRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDeviceRepository();
        assetService = new AssetService(repository);
    }

    @Test
    @DisplayName("Should throw DeviceNotFoundException when ID does not exist")
    void testRentNonExistentDevice() {
        // ACT & ASSERT
        assertThrows(DeviceNotFoundException.class, () -> {
            assetService.rentDevice("NON-EXISTENT-ID");
        });
    }

    @Test
    @DisplayName("Should throw InvalidDeviceStateException when device is already IN_USE")
    void testRentAlreadyInUse() {
        // ARRANGE
        MobilePhone phone = new MobilePhone("M1", "Apple", "iPhone 15", "iOS", "+123");
        assetService.registerNewDevice(phone);
        assetService.rentDevice("M1"); // First rental

        // ACT & ASSERT
        assertThrows(InvalidDeviceStateException.class, () -> {
            assetService.rentDevice("M1");
        });
    }
}
