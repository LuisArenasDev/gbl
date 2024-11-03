package com.gbl.device.repositories;

import com.gbl.device.models.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    @Query("SELECT d FROM Device d WHERE :brand IS NULL OR :brand = '' OR LOWER(d.deviceBrand) = LOWER(:brand)")
    Page<Device> findByDeviceBrandIgnoreCase(@Param("brand") String brand, Pageable pageable);
}
