package com.gbl.device.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbl.device.dto.DeviceRecordPatchRequestDto;
import com.gbl.device.dto.DeviceRecordRequestDto;
import com.gbl.device.models.Device;
import com.gbl.device.services.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(DeviceController.class)
class DeviceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    @Autowired
    private ObjectMapper objectMapper;

    private Device device;

    @BeforeEach
    void setUp() {
        device = new Device();
        device.setId(1L);
        device.setDeviceName("Test Device");
    }

    @Test
    void testAddDevice() throws Exception {
        DeviceRecordRequestDto requestDto = new DeviceRecordRequestDto("TestDeviceName", "TestDeviceBrand", LocalDateTime.now());
        when(deviceService.createDevice(any(DeviceRecordRequestDto.class))).thenReturn(device);

        mockMvc.perform(post("/api/device/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(device.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deviceName", is(device.getDeviceName())));
    }

    @Test
    void testGetDeviceById_Found() throws Exception {
        when(deviceService.getRecordById(anyLong())).thenReturn(Optional.of(device));

        mockMvc.perform(get("/api/device/{deviceId}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(device.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deviceName", is(device.getDeviceName())));
    }

    @Test
    void testGetDeviceById_NotFound() throws Exception {
        when(deviceService.getRecordById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/device/{deviceId}", 1L))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testUpdateDevice() throws Exception {
        DeviceRecordRequestDto requestDto = new DeviceRecordRequestDto("TestDeviceName", "TestDeviceBrand", LocalDateTime.now());
        when(deviceService.updateDevice(any(DeviceRecordRequestDto.class), anyLong())).thenReturn(device);

        mockMvc.perform(put("/api/device/{deviceId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(device.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deviceName", is(device.getDeviceName())));
    }

    @Test
    void testPatchDevice() throws Exception {
        DeviceRecordPatchRequestDto patchRequestDto = new DeviceRecordPatchRequestDto();
        when(deviceService.updateNonNullDeviceAttributes(any(DeviceRecordPatchRequestDto.class), anyLong())).thenReturn(device);

        mockMvc.perform(patch("/api/device/{deviceId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(device.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deviceName", is(device.getDeviceName())));
    }

    @Test
    void testListDevices() throws Exception {
        Page<Device> page = new PageImpl<>(List.of(device));
        when(deviceService.getAllRecordsByBrand(anyString(), anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/device/")
                        .param("brand", "TestBrand")
                        .param("pageSize", "10")
                        .param("pageNo", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id", is(device.getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].deviceName", is(device.getDeviceName())));
    }

    @Test
    void testDeleteById() throws Exception {
        Mockito.doNothing().when(deviceService).deleteDeviceById(anyLong());

        mockMvc.perform(delete("/api/device/{deviceId}", 1L))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
