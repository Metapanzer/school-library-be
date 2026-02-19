package com.smbc.school_library.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCatalogRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @NotBlank
    private String isbn;
    @NotBlank
    private String publisher;
    @Min(0)
    private Integer totalQty;
    @Min(0)
    private Integer availableQty;
}
