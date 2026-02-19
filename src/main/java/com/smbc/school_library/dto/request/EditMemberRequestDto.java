package com.smbc.school_library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class EditMemberRequestDto {
    @NotBlank
    private String fullName;
}
