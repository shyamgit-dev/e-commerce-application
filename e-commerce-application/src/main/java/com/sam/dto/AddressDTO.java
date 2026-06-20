package com.sam.dto;

import com.sam.constant.AddressType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private Long id;

    @NotBlank
    private String street;
    private String city;
    private String state;
    private String country;
    @Enumerated(EnumType.STRING)
    private AddressType addressType;
    private String zipCode;
    private boolean isDefault;
    private UsersDTO user;
}
