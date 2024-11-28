package com.gbl.device.controllers;

import com.gbl.device.dto.DeviceRecordPatchRequestDto;
import com.gbl.device.dto.DeviceRecordRequestDto;
import com.gbl.device.models.Device;
import com.gbl.device.services.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/V1/device")
@AllArgsConstructor
public class DeviceController {

    private DeviceService deviceService;

    @PostMapping("/")
    @Operation(summary = "Create a new device", description = "Create a new device resource by providing deviceName, deviceBrand and creationTime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created"),
    })
    public ResponseEntity<Device> addDevice(@Valid @RequestBody DeviceRecordRequestDto request) {
        return new ResponseEntity<>(deviceService.createDevice(request), HttpStatus.CREATED);
    }

    @GetMapping("/{deviceId}")
    @Operation(summary = "Get device by Id", description = "Get an existing device querying by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device found"),
            @ApiResponse(responseCode = "404", description = "Not found - The device was not found by provided Id")
    })
    public ResponseEntity<Device> getDeviceById(@PathVariable("deviceId") @NotNull Long id) {
        Optional<Device> record = deviceService.getRecordById(id);

        return record.map(device -> new ResponseEntity<>(device, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{deviceId}")
    @Operation(summary = "Update a device record", description = "Full update a device record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device updated"),
            @ApiResponse(responseCode = "404", description = "Not found - The device was not found by provided Id")
    })
    public ResponseEntity<Device> updateDevice(@PathVariable("deviceId") @NotNull Long id,
                                               @Valid @RequestBody DeviceRecordRequestDto request) {
        return new ResponseEntity<>(deviceService.updateDevice(request, id), HttpStatus.OK);
    }

    @PatchMapping("/{deviceId}")
    @Operation(summary = "Partially update a device record", description = "Partial update of a device record, might contain deviceName," +
            "deviceBrand or creationTime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device updated"),
            @ApiResponse(responseCode = "404", description = "Not found - The device was not found by provided Id")
    })
    public ResponseEntity<Device> patchDevice(@PathVariable("deviceId") @NotNull Long id,
                                               @RequestBody DeviceRecordPatchRequestDto request) {
        return new ResponseEntity<>(deviceService.updateNonNullDeviceAttributes(request, id), HttpStatus.OK);
    }

    @GetMapping("/")
    @Operation(summary = "List devices", description = "Query devices and return a paginated list of records," +
            " request might include brand String to query devices by brand")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    public ResponseEntity<Page<Device>> listDevices(
            @RequestParam(name="brand", required = false) String brand,
            @RequestParam(name="pageSize", defaultValue = "10") @Positive @Max(100) Integer pageSize,
            @RequestParam(name="pageNo", defaultValue = "0") @PositiveOrZero Integer pageNo) {
        return new ResponseEntity<>(deviceService.getAllRecordsByBrand(brand,
                pageSize, pageNo), HttpStatus.OK);
    }

    @DeleteMapping("/{deviceId}")
    @Operation(summary = "Delete device by Id", description = "Delete a device record by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted"),
    })
    public ResponseEntity<Void> deleteById(@PathVariable("deviceId") @NotNull Long id) {
        deviceService.deleteDeviceById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
