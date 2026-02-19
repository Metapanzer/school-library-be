package com.smbc.school_library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageRequestDto {
    @NotBlank
    private Integer page;
    @NotBlank
    private Integer pageSize;
}
