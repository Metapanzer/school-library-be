package com.smbc.school_library.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CatalogResponseDto {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private Integer totalQty;
    private Integer availableQty;
}
