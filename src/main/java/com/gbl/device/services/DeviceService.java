package com.gbl.device.services;

import com.gbl.device.dto.DeviceRecordPatchRequestDto;
import com.gbl.device.dto.DeviceRecordRequestDto;
import com.gbl.device.exception.DeviceNotFoundException;
import com.gbl.device.models.Device;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface DeviceService {

    Device createDevice(DeviceRecordRequestDto requset);

    Device updateDevice(DeviceRecordRequestDto request, Long id) throws DeviceNotFoundException;

    Device updateNonNullDeviceAttributes(DeviceRecordPatchRequestDto request, Long id) throws DeviceNotFoundException;

    Optional<Device> getRecordById(Long id);

    Page<Device> getAllRecordsByBrand(String brand,
                                      Integer pageSize,
                                      Integer pageNo);
    void deleteDeviceById(Long id);
}
