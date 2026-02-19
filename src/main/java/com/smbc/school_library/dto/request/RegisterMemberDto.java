package com.smbc.school_library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterMemberDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String fullName;
}
