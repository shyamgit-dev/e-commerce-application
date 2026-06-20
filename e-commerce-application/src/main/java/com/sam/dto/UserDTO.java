package com.sam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userId;

    @NotNull(message = "Name Can't be Empty")
    @Size(min = 3,max = 50)
    private String name;

    @NotNull(message = "Email Can't be Empty")
    @Email(message = "Invalid Email Format")
    private String email;

    private List<OrderDTO> orders;
}
