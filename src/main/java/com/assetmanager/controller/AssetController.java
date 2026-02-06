package com.assetmanager.controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assetmanager.model.Device;
import com.assetmanager.service.AssetService;

@RestController
@RequestMapping("/api/assets")
public class AssetController {
    
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public List<Device> getAllAssets() {
        return assetService.getAllDevices();
    }

    @PostMapping
    public ResponseEntity<Device> registerDevice(@RequestBody Device device) {
        assetService.registerNewDevice(device);
        Device createdDevice = assetService.getCreatedDevice(device.getDeviceId());

        // Returning 201 Created as per US-005 requirements
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

}
