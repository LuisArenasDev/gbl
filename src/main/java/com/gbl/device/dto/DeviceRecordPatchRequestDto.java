package com.gbl.device.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRecordPatchRequestDto {

    private String deviceName;

    private String deviceBrand;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String creationTime;
}
