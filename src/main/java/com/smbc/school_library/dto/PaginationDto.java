package com.smbc.school_library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationDto {
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
