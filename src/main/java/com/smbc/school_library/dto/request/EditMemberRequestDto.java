package com.smbc.school_library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EditMemberRequestDto {
    @NotBlank
    private String name;
}
