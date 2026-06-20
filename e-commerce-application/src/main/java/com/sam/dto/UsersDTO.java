package com.sam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersDTO {

    @NotNull(message = "Name Can't be Empty")
    @Size(min = 3,max = 50)
    private String name;

    @NotNull(message = "Email Can't be Empty")
    @Email(message = "Invalid Email Format")
    private String email;

    private boolean isActive;

}
