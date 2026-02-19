package com.smbc.school_library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditMemberRequestDto {
    @NotBlank
    private String fullName;
}
