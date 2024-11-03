package com.gbl.device.controllers;

import com.gbl.device.dto.DeviceRecordPatchRequestDto;
import com.gbl.device.dto.DeviceRecordRequestDto;
import com.gbl.device.models.Device;
import com.gbl.device.services.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class DeviceControllerTest {

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private DeviceController deviceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddDevice() {
        DeviceRecordRequestDto requestDto = new DeviceRecordRequestDto();
        Device device = new Device();

        when(deviceService.createDevice(any(DeviceRecordRequestDto.class))).thenReturn(device);

        ResponseEntity<Device> response = deviceController.addDevice(requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(device, response.getBody());
        verify(deviceService, times(1)).createDevice(requestDto);
    }

    @Test
    void testGetDeviceById_Found() {
        Device device = new Device();
        when(deviceService.getRecordById(anyLong())).thenReturn(Optional.of(device));

        ResponseEntity<Device> response = deviceController.getDeviceById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(device, response.getBody());
        verify(deviceService, times(1)).getRecordById(1L);
    }

    @Test
    void testGetDeviceById_NotFound() {
        when(deviceService.getRecordById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Device> response = deviceController.getDeviceById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(deviceService, times(1)).getRecordById(1L);
    }

    @Test
    void testUpdateDevice() {
        DeviceRecordRequestDto requestDto = new DeviceRecordRequestDto();
        Device device = new Device();

        when(deviceService.updateDevice(any(DeviceRecordRequestDto.class), anyLong())).thenReturn(device);

        ResponseEntity<Device> response = deviceController.updateDevice(1L, requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(device, response.getBody());
        verify(deviceService, times(1)).updateDevice(requestDto, 1L);
    }

    @Test
    void testPatchDevice() {
        DeviceRecordPatchRequestDto patchRequestDto = new DeviceRecordPatchRequestDto();
        Device device = new Device();

        when(deviceService.updateNonNullDeviceAttributes(any(DeviceRecordPatchRequestDto.class), anyLong())).thenReturn(device);

        ResponseEntity<Device> response = deviceController.patchDevice(1L, patchRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(device, response.getBody());
        verify(deviceService, times(1)).updateNonNullDeviceAttributes(patchRequestDto, 1L);
    }

    @Test
    void testListDevices() {
        Device device = new Device();
        Page<Device> page = new PageImpl<>(List.of(device));
        String brand = "SomeBrand";
        int pageSize = 10;
        int pageNo = 0;

        when(deviceService.getAllRecordsByBrand(anyString(), anyInt(), anyInt())).thenReturn(page);

        ResponseEntity<Page<Device>> response = deviceController.listDevices(brand, pageSize, pageNo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
        verify(deviceService, times(1)).getAllRecordsByBrand(brand, pageSize, pageNo);
    }

    @Test
    void testDeleteById() {
        doNothing().when(deviceService).deleteDeviceById(anyLong());

        ResponseEntity<Device> response = deviceController.deleteById(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deviceService, times(1)).deleteDeviceById(1L);
    }
}
