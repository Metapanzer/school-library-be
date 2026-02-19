package com.smbc.school_library.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RentResponseDto {
    private Long id;
    private MemberResponseDto member;
    private CatalogResponseDto catalog;
    private String rentDate;
    private String dueDate;
    private String returnDate;
}
