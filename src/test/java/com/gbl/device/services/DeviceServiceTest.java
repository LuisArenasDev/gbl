package com.gbl.device.services;

import com.gbl.device.dto.DeviceRecordPatchRequestDto;
import com.gbl.device.dto.DeviceRecordRequestDto;
import com.gbl.device.exception.DeviceNotFoundException;
import com.gbl.device.models.Device;
import com.gbl.device.repositories.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DeviceServiceImplTest {

    @Mock
    private DeviceRepository repository;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    private Device device;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        device = new Device();
        device.setId(1L);
        device.setDeviceName("Test Device");
        device.setDeviceBrand("Test Brand");
    }

    @Test
    void testCreateDevice() {
        DeviceRecordRequestDto requestDto = new DeviceRecordRequestDto();
        requestDto.setDeviceName("New Device");
        requestDto.setDeviceBrand("New Brand");

        when(repository.save(any(Device.class))).thenReturn(device);

        Device createdDevice = deviceService.createDevice(requestDto);

        assertNotNull(createdDevice);
        assertEquals("Test Device", createdDevice.getDeviceName());
        assertEquals("Test Brand", createdDevice.getDeviceBrand());
        verify(repository, times(1)).save(any(Device.class));
    }

    @Test
    void testUpdateDevice_Found() {
        DeviceRecordRequestDto requestDto = new DeviceRecordRequestDto();
        requestDto.setDeviceName("Updated Device");
        requestDto.setDeviceBrand("Updated Brand");

        when(repository.findById(anyLong())).thenReturn(Optional.of(device));
        when(repository.save(any(Device.class))).thenReturn(device);

        Device updatedDevice = deviceService.updateDevice(requestDto, 1L);

        assertEquals("Updated Device", updatedDevice.getDeviceName());
        assertEquals("Updated Brand", updatedDevice.getDeviceBrand());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(device);
    }

    @Test
    void testUpdateDevice_NotFound() {
        DeviceRecordRequestDto requestDto = new DeviceRecordRequestDto();
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class, () -> deviceService.updateDevice(requestDto, 1L));
        verify(repository, times(1)).findById(1L);
        verify(repository, never()).save(any(Device.class));
    }

    @Test
    void testUpdateNonNullDeviceAttributes() {
        DeviceRecordPatchRequestDto patchRequestDto = new DeviceRecordPatchRequestDto();
        patchRequestDto.setDeviceName("Patched Device");
        patchRequestDto.setDeviceBrand("Patched Brand");
        patchRequestDto.setCreationTime(LocalDateTime.now().toString());

        when(repository.findById(anyLong())).thenReturn(Optional.of(device));
        when(repository.save(any(Device.class))).thenReturn(device);

        Device patchedDevice = deviceService.updateNonNullDeviceAttributes(patchRequestDto, 1L);

        assertEquals("Patched Device", patchedDevice.getDeviceName());
        assertEquals("Patched Brand", patchedDevice.getDeviceBrand());
        assertNotNull(patchedDevice.getCreationTime());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(device);
    }

    @Test
    void testUpdateNonNullDeviceAttributes_NullValues() {
        DeviceRecordPatchRequestDto patchRequestDto = new DeviceRecordPatchRequestDto();
        patchRequestDto.setDeviceName(null);  // Should not update the device name
        patchRequestDto.setCreationTime("");  // Should set creationTime to null

        when(repository.findById(anyLong())).thenReturn(Optional.of(device));
        when(repository.save(any(Device.class))).thenReturn(device);

        Device patchedDevice = deviceService.updateNonNullDeviceAttributes(patchRequestDto, 1L);

        assertEquals("Test Device", patchedDevice.getDeviceName()); // Name should remain the same
        assertNull(patchedDevice.getCreationTime()); // Creation time should be set to null
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(device);
    }

    @Test
    void testGetRecordById_Found() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(device));

        Optional<Device> foundDevice = deviceService.getRecordById(1L);

        assertTrue(foundDevice.isPresent());
        assertEquals(device, foundDevice.get());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testGetRecordById_NotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Device> foundDevice = deviceService.getRecordById(1L);

        assertFalse(foundDevice.isPresent());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testGetAllRecordsByBrand() {
        Page<Device> page = new PageImpl<>(List.of(device));
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findByDeviceBrandIgnoreCase(anyString(), any(Pageable.class))).thenReturn(page);

        Page<Device> result = deviceService.getAllRecordsByBrand("Test Brand", 10, 0);

        assertEquals(1, result.getTotalElements());
        assertEquals(device, result.getContent().get(0));
        verify(repository, times(1)).findByDeviceBrandIgnoreCase("Test Brand", pageable);
    }

    @Test
    void testDeleteDeviceById() {
        doNothing().when(repository).deleteById(anyLong());

        deviceService.deleteDeviceById(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}
