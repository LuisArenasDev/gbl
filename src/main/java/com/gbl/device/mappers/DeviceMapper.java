package com.gbl.device.mappers;

import com.gbl.device.dto.DeviceRecordRequestDto;
import com.gbl.device.models.Device;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DeviceMapper {
    DeviceMapper INSTANCE = Mappers.getMapper( DeviceMapper.class );

    Device requestToModel(DeviceRecordRequestDto request);

}
