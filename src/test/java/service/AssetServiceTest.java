package service;

// Standard JUnit 5 Imports
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.assetmanager.exception.DeviceNotFoundException;
import com.assetmanager.exception.InvalidDeviceStateException;
import com.assetmanager.model.Device;
import com.assetmanager.model.DeviceStatus;
import com.assetmanager.model.MobilePhone;
import com.assetmanager.repository.DeviceRepository;
import com.assetmanager.service.AssetService;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private DeviceRepository repository; // The "stunt double"

    @InjectMocks
    private AssetService assetService; // The "brain" with the mock inside

    @Test
    @DisplayName("Should successfully Register a device")
    void testRegisterDevice() {
        // 1. ARRANGE
        String deviceId = "M1";
        MobilePhone phone = new MobilePhone(deviceId, "Apple", "iPhone 15", "iOS", "+123");

        when(repository.existsById(deviceId)).thenReturn(false);

        // 2. ACT
        assetService.registerNewDevice(phone);

        // 3. ASSERT
        assertEquals(DeviceStatus.AVAILABLE, phone.getStatus());

        // 4. VERIFY

        // Verify Registration
        verify(repository,times(1)).save(phone);

        // Verify that the service checked for existing ID
        verify(repository,times(1)).existsById(deviceId);
    }

    @Test
    @DisplayName("Should successfully RENT a device")
    void testRentDevice() {
        // 1. ARRANGE
        String deviceId = "M1";
        MobilePhone phone = new MobilePhone(deviceId, "Apple", "iPhone 15", "iOS", "+123");
        when(repository.findById(deviceId)).thenReturn(Optional.of(phone));

        // 2. ACT
        assetService.rentDevice(deviceId);

        // 3. ASSERT
        assertEquals(DeviceStatus.IN_USE, phone.getStatus());

        // 4. VERIFY

        // Verify Registration: Was save() called when we registered?
        verify(repository,times(1)).save(phone);

        // Verify Rental Flow: Did the service look for the device?
        verify(repository,times(1)).findById(deviceId);
    }

    @Test
    @DisplayName("Should throw DeviceNotFoundException when ID does not exist")
    void testRentNonExistentDevice() {
        // 1. ARRANGE
        String nonExistentId = "UNKNOWN-99";

        // Train the mock: When searching for this ID, return an empty Optional
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());
        
        // 2. ACT & ASSERT
        // We verify the SERVICE logic: it should detect the empty optional and throw the custom exception
        assertThrows(DeviceNotFoundException.class, () -> {
            assetService.rentDevice(nonExistentId);
        });

        // 3. VERIFY (Interaction)
        // Ensure the service at least TRIED to look for it
        verify(repository).findById(nonExistentId);
    
        // Ensure the service NEVER tried to save anything (since it didn't find the device)
        verify(repository, never()).save(any(Device.class));
    }

    @Test
    @DisplayName("Should throw InvalidDeviceStateException when device is already IN_USE")
    void testRentAlreadyInUse() {
        // 1. ARRANGE
        String deviceId = "M1";
        MobilePhone phone = new MobilePhone(deviceId, "Apple", "iPhone 15", "iOS", "+123");
    
        // Crucial step: We MANUALLY set the state to IN_USE to simulate the scenario
        // This replaces the need to call assetService.registerNewDevice() and assetService.rentDevice()
        phone.rent(); 

        // Train the mock to return the already rented phone
        when(repository.findById(deviceId)).thenReturn(Optional.of(phone));

        // 2. ACT & ASSERT
        assertThrows(InvalidDeviceStateException.class, () -> {
            assetService.rentDevice(deviceId);
        });

        // 3. VERIFY
        // Check that the service correctly consulted the repository
        verify(repository).findById(deviceId);
    
        // IMPORTANT: Verify that save() was NEVER called a second time
        // because the logic should have failed before reaching the persistence step
        verify(repository, never()).save(any(Device.class));

    }

    @Test
    @DisplayName("Should successfully rent and return a device")
    void testRentAndReturnDevice() {
        // 1. ARRANGE
        String deviceId = "M1";
        MobilePhone phone = new MobilePhone(deviceId, "Apple", "iPhone 15", "iOS", "+123");

        when(repository.findById(deviceId)).thenReturn(Optional.of(phone));

        // 2.1 ACT
        assetService.rentDevice(deviceId);

        // 3.1 ASSERT after renting
        assertEquals(DeviceStatus.IN_USE, phone.getStatus());

        // 2.2 ACT - return the device
        assetService.returnDevice(deviceId);

        // 3.2 ASSERT after returning
        assertEquals(DeviceStatus.AVAILABLE, phone.getStatus());

        // 4. VERIFY
        verify(repository,times(2)).save(phone);

        // Verify Rental Flow: Did the service look for the device?
        verify(repository, times(2)).findById(deviceId);
    }

    @Test
    @DisplayName("Should throw InvalidDeviceStateException when returning a device that is not IN_USE")
    void testReturnDeviceNotInUse() {
        // 1. ARRANGE
        String deviceId = "M1";
        MobilePhone phone = new MobilePhone(deviceId, "Apple", "iPhone 15", "iOS", "+123");
        //assetService.registerNewDevice(phone);

        when(repository.findById(deviceId)).thenReturn(Optional.of(phone));

        // 2. ACT & ASSERT
        assertThrows(InvalidDeviceStateException.class, () -> {
            assetService.returnDevice(deviceId);
        });

        assertEquals(DeviceStatus.AVAILABLE, phone.getStatus(), "Device status should remain AVAILABLE");

        // 3. VERIFY
        verify(repository, times(1)).findById(deviceId);
        verify(repository, never()).save(any(Device.class));
    }

    @Test
    @DisplayName("Should throw DeviceNotFoundException when returning a non-existent device")
    void testReturnNonExistentDevice() {
        // 1. ARRANGE
        String deviceId = "NON-EXISTENT-ID";
        
        when(repository.findById(deviceId)).thenReturn(Optional.empty());

        // 2. ACT & ASSERT
        assertThrows(DeviceNotFoundException.class, () -> {
            assetService.returnDevice(deviceId);
        });

        // 3. VERIFY
        verify(repository, times(1)).findById(deviceId);
        verify(repository, never()).save(any(Device.class));
    }

    @Test
    @DisplayName("Should list only available devices after rent and return operations")
    void testListAvailableDevices() {
        // 1. ARRANGE
        String deviceId1 = "M1";
        String deviceId2 = "M2";
        String deviceId3 = "M3"; 
        MobilePhone phone1 = new MobilePhone(deviceId1, "Apple", "iPhone 15", "iOS", "+123");
        MobilePhone phone2 = new MobilePhone(deviceId2, "Samsung", "Galaxy S24", "Android", "+456");
        MobilePhone phone3 = new MobilePhone(deviceId3, "Google", "Pixel 8", "Android", "+789");

        phone2.rent(); 
        phone3.sendToMaintenance("Broken screen");

        when(repository.findByStatus(DeviceStatus.AVAILABLE)).thenReturn(List.of(phone1));

        // 2. ACT
        List<Device> available = assetService.getAllAvailableDevices();

        // 3. ASSERT
        assertEquals(1, available.size(), "Only one device should be available");
        assertEquals(deviceId1, available.get(0).getDeviceId());

        assertFalse(available.stream().anyMatch(d -> d.getDeviceId().equals(deviceId2)));
        assertFalse(available.stream().anyMatch(d -> d.getDeviceId().equals(deviceId3)));

        // 4. VERIFY
        verify(repository, times(1)).findByStatus(DeviceStatus.AVAILABLE);
        verify(repository, never()).save(any(Device.class));
    }

    @Test
    @DisplayName("Should successfully move device from available to maintenance and complete repair")
    void testMoveAvailableDeviceToMaintenanceAndCompleteRepair() {
        // 1. ARRANGE
        String deviceId = "M1";
        MobilePhone phone = new MobilePhone(deviceId, "Apple", "iPhone 15", "iOS", "+123");

        when(repository.findById(deviceId)).thenReturn(Optional.of(phone));

        // 2.1 ACT - move to maintenance
        assetService.moveDeviceToMaintenance(deviceId, "Screen issue");

        // 3.1 ASSERT after moving to maintenance
        assertEquals(DeviceStatus.UNDER_REPAIR, phone.getStatus());
        assertEquals("Screen issue", phone.getMaintenanceReason());

        // 2.2 ACT - complete repair
        assetService.completeDeviceRepair(deviceId);

        // 3.2 ASSERT after completing repair
        assertEquals(DeviceStatus.AVAILABLE, phone.getStatus());
        assertNull(phone.getMaintenanceReason());

        // 3. VERIFY
        verify(repository, times(2)).findById(deviceId);
        verify(repository, times(2)).save(phone);
    }

    @Test
    @DisplayName("Should successfully move device from in_use to maintenance and complete repair")
    void testMoveInUseDeviceToMaintenanceAndCompleteRepair() {
        // 1. ARRANGE
        String deviceId = "M1";
        MobilePhone phone = new MobilePhone(deviceId, "Apple", "iPhone 15", "iOS", "+123");

        when(repository.findById(deviceId)).thenReturn(Optional.of(phone));

        // 2.1 ACT - First, rent the device
        assetService.rentDevice(deviceId);

        // 3.1 ASSERT - after renting
        assertEquals(DeviceStatus.IN_USE, phone.getStatus(), "Device should be IN_USE after renting");

        // 2.2 ACT - move to maintenance
        assetService.moveDeviceToMaintenance(deviceId, "Screen issue");

        // 3.2 ASSERT - after moving to maintenance
        assertEquals(DeviceStatus.UNDER_REPAIR, phone.getStatus(), "Device should be UNDER_REPAIR after moving to maintenance");
        assertEquals("Screen issue", phone.getMaintenanceReason(), "Maintenance reason should match");

        // 2.3 ACT - complete repair
        assetService.completeDeviceRepair(deviceId);

        // 3.3 ASSERT - after completing repair
        assertEquals(DeviceStatus.AVAILABLE, phone.getStatus(), "Device should be AVAILABLE after repair");
        assertNull(phone.getMaintenanceReason(), "Maintenance reason should be cleared after repair");

        // 4. VERIFY
        verify(repository, times(3)).findById(deviceId);
        verify(repository, times(3)).save(phone); 
    }

    @Test
    @DisplayName("Should throw InvalidDeviceStateException when renting a device that is under maintenance")
    void testRentDeviceUnderMaintenance() {
        // 1. ARRANGE
        String deviceId1 = "M1";
        MobilePhone phone = new MobilePhone(deviceId1, "Apple", "iPhone 15", "iOS", "+123");

        when(repository.findById(deviceId1)).thenReturn(Optional.of(phone));

        // 2.1 ACT - Move device to maintenance
        assetService.moveDeviceToMaintenance(deviceId1, "Screen issue");

        // 3.1 ASSERT - Now try to rent the device which is under maintenance
        assertEquals(DeviceStatus.UNDER_REPAIR, phone.getStatus(), "Device should be UNDER_REPAIR before renting attempt");

        // 2.2 ACT & 3.2 ASSERT
        assertThrows(InvalidDeviceStateException.class, () -> {
            assetService.rentDevice(deviceId1);
        });

        // 4. VERIFY
        verify(repository, times(2)).findById(deviceId1);
        verify(repository, times(1)).save(phone);
    }

    @Test
    @DisplayName("Should throw InvalidDeviceStateException when returning a device that is under maintenance")
    void testReturnDeviceUnderMaintenance() {
        // 1. ARRANGE
        String deviceId = "M1";
        MobilePhone phone = new MobilePhone(deviceId, "Apple", "iPhone 15", "iOS", "+123");

        when(repository.findById(deviceId)).thenReturn(Optional.of(phone));

        // 2.1 ACT - Move device to maintenance
        assetService.moveDeviceToMaintenance(deviceId, "Screen issue");

        // 3.1 ASSERT - Now try to return the device which is under maintenance
        assertEquals(DeviceStatus.UNDER_REPAIR, phone.getStatus(), "Device should be UNDER_REPAIR before return attempt");


        // 2.2 ACT & 3.2 ASSERT
        assertThrows(InvalidDeviceStateException.class, () -> {
            assetService.returnDevice(deviceId);
        });

        // 4. VERIFY
        verify(repository, times(2)).findById(deviceId);
        verify(repository, times(1)).save(phone);
    }

    @Test
    @DisplayName("Should show the reason for maintenance when listing devices")
    void testShowMaintenanceReason() {
        // 1. ARRANGE
        String deviceId1 = "M1";
        String deviceId2 = "M2";
        MobilePhone phone1 = new MobilePhone(deviceId1, "Apple", "iPhone 15", "iOS", "+123");

        when(repository.findByStatus(DeviceStatus.UNDER_REPAIR)).thenReturn(List.of(phone1));

        // 2. ACT - Move device to maintenance
        phone1.sendToMaintenance("Screen issue");
        List<Device> maintenanceDevices = assetService.getAllOnMaintenanceDevices();


        // 3. ASSERT
        assertEquals(1, maintenanceDevices.size(), "Only one device should be under maintenance");
        assertEquals(deviceId1, maintenanceDevices.get(0).getDeviceId());
        assertEquals("Screen issue", maintenanceDevices.get(0).getMaintenanceReason(), "Maintenance reason should match");

        assertFalse(maintenanceDevices.stream().anyMatch(d -> d.getDeviceId().equals(deviceId2)));

        // 4. VERIFY
        verify(repository, times(1)).findByStatus(DeviceStatus.UNDER_REPAIR);
        verify(repository, never()).save(any(Device.class));
    }

    @Test
    @DisplayName("Should throw DeviceNotFoundException when moving non-existent device to decommission")
    void testMoveNonExistentDeviceToDecommission() {
        // 1. ARRANGE
        String deviceId = "NON-EXISTENT-ID";
        
        when(repository.findById(deviceId)).thenReturn(Optional.empty());
        // 2. ACT & ASSERT
        assertThrows(DeviceNotFoundException.class, () -> {
            assetService.decommissionDevice(deviceId);
        });
        // 3. VERIFY
        verify(repository, times(1)).findById(deviceId);
        verify(repository, never()).save(any(Device.class));
    }

    @Test
    @DisplayName("Should successfully decommission an available device")
    void testDecommissionAvailableDevice() {
        // 1. ARRANGE
        String deviceId = "M1";
        MobilePhone phone = new MobilePhone(deviceId, "Apple", "iPhone 15", "iOS", "+123");

        when(repository.findById(deviceId)).thenReturn(Optional.of(phone));

        // 2. ACT - Decommission the device
        assetService.decommissionDevice(deviceId);

        // 3. ASSERT
        assertEquals(DeviceStatus.DECOMMISSIONED, phone.getStatus(), "Device should be DECOMMISSIONED");

        // 4. VERIFY
        verify(repository, times(1)).findById(deviceId);
        verify(repository, times(1)).save(phone);
    }

    @Test
    @DisplayName("Should throw InvalidDeviceStateException when trying to decommission a device that is already decommissioned")
    void testDecommissionAlreadyDecommissionedDevice() {
        // 1. ARRANGE
        String deviceId = "M1";
        MobilePhone phone = new MobilePhone(deviceId, "Apple", "iPhone 15", "iOS", "+123");
        phone.decommission();

        when(repository.findById(deviceId)).thenReturn(Optional.of(phone));

        // 2. ACT & ASSERT
        assertThrows(InvalidDeviceStateException.class, () -> {
            assetService.decommissionDevice(deviceId);
        });

        // 3. VERIFY
        verify(repository, times(1)).findById(deviceId);
        verify(repository, never()).save(any(Device.class));
    }

    @Test
    @DisplayName("Should throw InvalidDeviceStateException when trying to decommission a device that is in use")
    void testDecommissionDeviceInUse() {
        // 1. ARRANGE
        String deviceId = "M1";
        MobilePhone phone = new MobilePhone(deviceId, "Apple", "iPhone 15", "iOS", "+123");
        phone.rent();

        when(repository.findById(deviceId)).thenReturn(Optional.of(phone));

        // 2. ACT & ASSERT
        assertThrows(InvalidDeviceStateException.class, () -> {
            assetService.decommissionDevice(deviceId);
        });

        // 3. VERIFY
        verify(repository, times(1)).findById(deviceId);
        verify(repository, never()).save(any(Device.class));
    }

    @Test
    @DisplayName("Should throw InvalidDeviceStateException when trying to rent a decommissioned device")
    void testRentDecommissionedDevice() {
        // 1. ARRANGE
        String deviceId = "M1";
        MobilePhone phone = new MobilePhone(deviceId, "Apple", "iPhone 15", "iOS", "+123");
        phone.decommission();

        when(repository.findById(deviceId)).thenReturn(Optional.of(phone));

        // 2. ACT & ASSERT
        assertThrows(InvalidDeviceStateException.class, () -> {
            assetService.rentDevice(deviceId);
        });

        // 3. VERIFY
        verify(repository, times(1)).findById(deviceId);
        verify(repository, never()).save(any(Device.class));
    }

    @Test
    @DisplayName("Should throw InvalidDeviceStateException when trying to return a decommissioned device")
    void testReturnDecommissionedDevice() {
        // 1. ARRANGE
        String deviceId = "M1";
        MobilePhone phone = new MobilePhone(deviceId, "Apple", "iPhone 15", "iOS", "+123");
        phone.decommission();

        when(repository.findById(deviceId)).thenReturn(Optional.of(phone));

        // 2. ACT & ASSERT
        assertThrows(InvalidDeviceStateException.class, () -> {
            assetService.returnDevice(deviceId);
        });

        // 3. VERIFY
        verify(repository, times(1)).findById(deviceId);
        verify(repository, never()).save(any(Device.class));
    }

    @Test
    @DisplayName("Should successfully match the date of decommissioned device")
    void testDecommissionedDeviceDate() {
        // 1. ARRANGE
        String deviceId = "M1";
        MobilePhone phone = new MobilePhone(deviceId, "Apple", "iPhone 15", "iOS", "+123");

        when(repository.findById(deviceId)).thenReturn(Optional.of(phone));

        // 2. ACT - Decommission the device
        assetService.decommissionDevice(deviceId);

        // 3. ASSERT
        assertEquals(DeviceStatus.DECOMMISSIONED, phone.getStatus(), "Device should be DECOMMISSIONED");
        assertNotNull(phone.getDecommissionDate(), "Decommission date should be set");
        assertTrue(phone.getDecommissionDate().isEqual(LocalDate.now()), "Date should be today");

        // 4. VERIFY
        verify(repository, times(1)).findById(deviceId);
        verify(repository, times(1)).save(phone);
    }
}