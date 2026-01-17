package service;

// Standard JUnit 5 Imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.DeviceNotFoundException;
import repository.InMemoryDeviceRepository;
import exception.InvalidDeviceStateException;
import model.Device;
import model.DeviceStatus;
import model.MobilePhone;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;


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

    @Test
    @DisplayName("Should successfully rent and return a device")
    void testRentAndReturnDevice() {
        // ARRANGE
        MobilePhone phone = new MobilePhone("M1", "Apple", "iPhone 15", "iOS", "+123");
        assetService.registerNewDevice(phone);

        // ACT & ASSERT
        assertDoesNotThrow(() -> {
            assetService.rentDevice("M1");
            assetService.returnDevice("M1");
        });
    }

    @Test
    @DisplayName("Should throw InvalidDeviceStateException when returning a device that is not IN_USE")
    void testReturnDeviceNotInUse() {
        // ARRANGE
        MobilePhone phone = new MobilePhone("M1", "Apple", "iPhone 15", "iOS", "+123");
        assetService.registerNewDevice(phone);

        // ACT & ASSERT
        assertThrows(InvalidDeviceStateException.class, () -> {
            assetService.returnDevice("M1");
        });
    }

    @Test
    @DisplayName("Should throw DeviceNotFoundException when returning a non-existent device")
    void testReturnNonExistentDevice() {
        // ACT & ASSERT
        assertThrows(DeviceNotFoundException.class, () -> {
            assetService.returnDevice("NON-EXISTENT-ID");
        });
    }

    @Test
    @DisplayName("Should list only available devices after rent and return operations")
    void testListAvailableDevices() {
        // ARRANGE
        MobilePhone phone1 = new MobilePhone("M1", "Apple", "iPhone 15", "iOS", "+123");
        MobilePhone phone2 = new MobilePhone("M2", "Samsung", "Galaxy S24", "Android", "+456");
        assetService.registerNewDevice(phone1);
        assetService.registerNewDevice(phone2);
        assetService.rentDevice("M1");

        // ACT
        assertDoesNotThrow(() -> assetService.returnDevice("M1"));

        // ASSERT
        List<Device> available = assetService.getAllAvailableDevices();
        assertTrue(available.stream().anyMatch(d -> d.getDeviceId().equals("M1")), "Device M1 should be available");
        assertTrue(available.stream().anyMatch(d -> d.getDeviceId().equals("M2")), "Device M2 should be available");
    }

    @Test
    @DisplayName("Should successfully move device from available to maintenance and complete repair")
    void testMoveAvailableDeviceToMaintenanceAndCompleteRepair() {
        // ARRANGE
        MobilePhone phone = new MobilePhone("M1", "Apple", "iPhone 15", "iOS", "+123");
        assetService.registerNewDevice(phone);

        // ACT & ASSERT
        assertDoesNotThrow(() -> {
            assetService.moveDeviceToMaintenance("M1", "Screen issue");
            assetService.completeDeviceRepair("M1");
        });
    }

    @Test
    @DisplayName("Should successfully move device from in_use to maintenance and complete repair")
    void testMoveInUseDeviceToMaintenanceAndCompleteRepair() {
        // ARRANGE
        MobilePhone phone = new MobilePhone("M1", "Apple", "iPhone 15", "iOS", "+123");
        assetService.registerNewDevice(phone);
        assetService.rentDevice("M1");

        // ACT 
        assetService.moveDeviceToMaintenance("M1", "Screen issue");
        assetService.completeDeviceRepair("M1");
        
        // ASSERT
        Device retrieved = repository.findById("M1").orElseThrow();

        assertEquals(DeviceStatus.AVAILABLE, retrieved.getStatus(), "Device should be AVAILABLE after repair");
        assertNotNull(retrieved.getMaintenanceReason(), "Maintenance reason should be cleared after repair");
    }

    @Test
    @DisplayName("Should throw InvalidDeviceStateException when renting a device that is under maintenance")
    void testRentDeviceUnderMaintenance() {
        // ARRANGE
        MobilePhone phone = new MobilePhone("M1", "Apple", "iPhone 15", "iOS", "+123");
        assetService.registerNewDevice(phone);
        assetService.moveDeviceToMaintenance("M1", "Screen issue");
        // ACT & ASSERT
        assertThrows(InvalidDeviceStateException.class, () -> {
            assetService.rentDevice("M1");
        });
    }

    @Test
    @DisplayName("Should throw InvalidDeviceStateException when returning a device that is under maintenance")
    void testReturnDeviceUnderMaintenance() {
        // ARRANGE
        MobilePhone phone = new MobilePhone("M1", "Apple", "iPhone 15", "iOS", "+123");
        assetService.registerNewDevice(phone);
        assetService.moveDeviceToMaintenance("M1", "Screen issue");
        // ACT & ASSERT
        assertThrows(InvalidDeviceStateException.class, () -> {
            assetService.returnDevice("M1");
        });
    }

    @Test
    @DisplayName("Should show the reason for maintenance when listing devices")
    void testShowMaintenanceReason() {
        // ARRANGE
        MobilePhone phone = new MobilePhone("M1", "Apple", "iPhone 15", "iOS", "+123");
        assetService.registerNewDevice(phone);
        assetService.moveDeviceToMaintenance("M1", "Screen issue"); 
        // ACT & ASSERT
        Device retrieved = repository.findById("M1").orElseThrow();
        assertEquals("Screen issue", retrieved.getMaintenanceReason(), "Maintenance reason should match");
    }

}