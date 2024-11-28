package com.gbl.device.services;

import com.gbl.device.dto.DeviceRecordPatchRequestDto;
import com.gbl.device.dto.DeviceRecordRequestDto;
import com.gbl.device.exception.DeviceNotFoundException;
import com.gbl.device.mappers.DeviceMapper;
import com.gbl.device.models.Device;
import com.gbl.device.repositories.DeviceRepository;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    DeviceRepository repository;

    @Override
    public Device createDevice(DeviceRecordRequestDto record) {
        return repository.save(DeviceMapper.INSTANCE.requestToModel(record));
    }

    @Override
    public Device updateDevice(DeviceRecordRequestDto request, Long id) throws DeviceNotFoundException{
        Device device = repository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with id: " + id));
        device.setDeviceName(request.getDeviceName());
        device.setDeviceBrand(request.getDeviceBrand());
        repository.save(device);
        return device;
    }

    @Override
    public Device updateNonNullDeviceAttributes(DeviceRecordPatchRequestDto request, Long id) throws DeviceNotFoundException{
        Device device = repository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with id: " + id));

        Optional.ofNullable(request.getDeviceName()).ifPresent(device::setDeviceName);
        Optional.ofNullable(request.getDeviceBrand()).ifPresent(device::setDeviceBrand);

        if(request.getCreationTime() != null){
            if(StringUtils.isEmpty(request.getCreationTime())){
                device.setCreationTime(null);
            } else {
                device.setCreationTime(LocalDateTime.parse(request.getCreationTime(), DateTimeFormatter.ISO_DATE_TIME));
            }
        }
        repository.save(device);
        return device;
    }

    @Override
    public Optional<Device> getRecordById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<Device> getAllRecordsByBrand(String brand,
                                             Integer pageSize, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return repository.findByDeviceBrandIgnoreCase(brand, pageable);
    }

    @Override
    public void deleteDeviceById(Long id) {
        repository.deleteById(id);
    }

}
