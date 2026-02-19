package com.smbc.school_library.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberResponseDto {
    private Long id;
    private String fullName;
    private String email;
}
