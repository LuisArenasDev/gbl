package com.gbl.device.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class DeviceRecordPatchRequestDto {

    private String deviceName;

    private String deviceBrand;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String creationTime;
}
