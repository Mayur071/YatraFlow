package com.yatraflow.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LoginRequest {


    @NotBlank(message = "Email is required.")
    @Email(message = "Please enter a valid email address.")
    @Size(max = 150, message = "Email cannot Exceed 150 characters")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 20, message = "password must be between 8 and 20 character.")
    private String password;
}
