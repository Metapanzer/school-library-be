package com.smbc.school_library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PageRequestDto {
    @NotBlank
    private Integer page;
    @NotBlank
    private Integer pageSize;
}
