package com.gbl.device.controllers;

import com.gbl.device.dto.DeviceRecordPatchRequestDto;
import com.gbl.device.dto.DeviceRecordRequestDto;
import com.gbl.device.models.Device;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DeviceControllerIntegrationTest {
    static final MySQLContainer mysqlContainer;

    @Autowired
    WebTestClient webTestClient;



    static {
        mysqlContainer = new MySQLContainer("mysql:latest");
        mysqlContainer.start();
    }

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto",() -> "create");

    }

    @Test
    @Order(1)
    void testAddDevice(){
        LocalDateTime now = LocalDateTime.now();
        DeviceRecordRequestDto requestDto = new DeviceRecordRequestDto("TestDeviceName", "TestDeviceBrand", now);

        webTestClient.post()
                .uri("/api/V1/device/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectStatus()
                .isCreated()
                .expectBody(Device.class)
                .consumeWith(deviceEntity -> Assertions.assertNotNull(Objects.requireNonNull(deviceEntity.getResponseBody()).getId()));
    }

    @Test
    @Order(2)
    void testGetDeviceById_Found() {
        webTestClient.get()
                .uri("/api/V1/device/{deviceId}", 1)
                .exchange()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectStatus()
                .isOk()
                .expectBody(Device.class)
                .consumeWith(deviceEntity -> Assertions.assertNotNull(Objects.requireNonNull(deviceEntity.getResponseBody()).getId()));
    }

    @Test
    @Order(3)
    void testGetDeviceById_NotFound() {
        webTestClient.get()
                .uri("/api/V1/device/{deviceId}", 2)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(Void.class);
    }

    @Test
    @Order(4)
    void testUpdateDevice(){
        LocalDateTime now = LocalDateTime.now();
        DeviceRecordRequestDto requestDto = new DeviceRecordRequestDto("TestDeviceNameUpdated", "TestDeviceBrandUpdated", now);

        webTestClient.put()
                .uri("/api/V1/device/{deviceId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectStatus()
                .isOk()
                .expectBody(Device.class)
                .consumeWith(deviceEntity -> Assertions.assertNotNull(Objects.requireNonNull(deviceEntity.getResponseBody()).getId()));
    }

    @Test
    @Order(5)
    void testPatchDevice(){
        DeviceRecordPatchRequestDto requestDto = DeviceRecordPatchRequestDto.builder().deviceName("TestDeviceNamePatched").build();

        webTestClient.patch()
                .uri("/api/V1/device/{deviceId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectStatus()
                .isOk()
                .expectBody(Device.class)
                .consumeWith(deviceEntity -> Assertions.assertNotNull(Objects.requireNonNull(deviceEntity.getResponseBody()).getId()));
    }

    @Test
    @Order(6)
    void testListDevices(){
        webTestClient.get()
                .uri("/api/V1/device/")
                .exchange()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectStatus()
                .isOk();
    }
    @Test
    @Order(7)
    void testDeleteById() throws Exception {
        webTestClient.delete()
                .uri("/api/V1/device/{deviceId}", 1L)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}
