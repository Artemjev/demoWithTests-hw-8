package com.example.demowithtests.dto.employee;

import com.example.demowithtests.domain.Gender;
import com.example.demowithtests.dto.address.AddressReadDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class EmployeePutDto {
//    @NotNull(message = "Name may not be null")
//    @Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters long")
//    @Schema(description = "Name of an employee.", example = "Billy", required = true)
    private String name;
    private String country;
//    @Email
//    @NotNull
    private String email;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Set<AddressReadDto> addresses = new HashSet<>();
    private LocalDateTime datetime = LocalDateTime.now();
    private Boolean isDeleted;
    private Boolean isPrivate;
    private Boolean isConfirmed;
}
