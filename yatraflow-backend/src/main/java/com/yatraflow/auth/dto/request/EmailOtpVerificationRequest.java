package com.yatraflow.auth.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailOtpVerificationRequest {

    @NotBlank(message = "Email is Required")
    @Size(max = 150, message = "Email Cannot exceed 150 characters.")
    @Email(message = "Please provide valid Email Address")
    private String email;



    @NotBlank(message = "OTP is Required")
    @Pattern(
            regexp = "^\\d{6}$",
            message = "OTP must contain exactly 6 digits."
    )
    private String otp;
}
