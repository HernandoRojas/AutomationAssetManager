package com.assetmanager.controller;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assetmanager.model.Device;
import com.assetmanager.model.DeviceStatus;
import com.assetmanager.service.AssetService;

@RestController
@RequestMapping("/api/assets")
public class AssetController {
    
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices(
    @RequestParam(required = false) DeviceStatus status,
    @RequestParam(required = false) String brand
    ) {
    // If status is provided, filter; otherwise, return all.
    return ResponseEntity.ok(assetService.findByStatusAndBrand(status, brand));
    }


    @PostMapping
    public ResponseEntity<Device> registerDevice(@RequestBody Device device) {
        assetService.registerNewDevice(device);
        Device createdDevice = assetService.getCreatedDevice(device.getDeviceId());
        return new ResponseEntity<>(createdDevice, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/decommission")
    public ResponseEntity<Device> decommissionDevice(@PathVariable String id) {
        assetService.decommissionDevice(id);
        Device decommissionedDevice = assetService.getCreatedDevice(id);
        return new ResponseEntity<>(decommissionedDevice, HttpStatus.OK);
    }

    @PostMapping("/{id}/rent")
    public ResponseEntity<Device> rentDevice(@PathVariable String id) {
        assetService.rentDevice(id);
        Device rentedDevice = assetService.getCreatedDevice(id);
        return new ResponseEntity<>(rentedDevice, HttpStatus.OK);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<Device> returnDevice(@PathVariable String id) {
        assetService.returnDevice(id);
        Device returnedDevice = assetService.getCreatedDevice(id);
        return new ResponseEntity<>(returnedDevice, HttpStatus.OK);
    }

    @PostMapping("/{id}/maintenance")
    public ResponseEntity<Device> moveDeviceToMaintenance(@PathVariable String id, @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        assetService.moveDeviceToMaintenance(id, reason);
        Device maintenanceDevice = assetService.getCreatedDevice(id);
        return new ResponseEntity<>(maintenanceDevice, HttpStatus.OK);
    }
    
    @PatchMapping("/{id}/maintenance/complete")
    public ResponseEntity<Device> completeMaintenance(@PathVariable String id) {
        assetService.completeDeviceRepair(id);
        Device completedDevice = assetService.getCreatedDevice(id);
        return new ResponseEntity<>(completedDevice, HttpStatus.OK);
    }

}