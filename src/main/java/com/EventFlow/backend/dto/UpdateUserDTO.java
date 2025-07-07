package com.EventFlow.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDTO {
    @Email(message = "Invalid email format") 
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    private String description;
}
