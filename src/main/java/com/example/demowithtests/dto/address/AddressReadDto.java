package com.example.demowithtests.dto.address;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressReadDto {
    private Long id;
    private Boolean addressHasActive = Boolean.TRUE;
    private String country;
    private String city;
    private String street;
    private LocalDateTime datetime = LocalDateTime.now();
}
