package com.smbc.school_library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginMemberDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
