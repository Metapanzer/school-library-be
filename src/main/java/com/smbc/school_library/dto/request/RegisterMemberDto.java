package com.smbc.school_library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterMemberDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String fullName;
}
