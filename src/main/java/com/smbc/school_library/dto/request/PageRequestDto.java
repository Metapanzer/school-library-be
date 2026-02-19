package com.smbc.school_library.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageRequestDto {
    @NotNull
    private Integer page;
    @NotNull
    private Integer pageSize;
}
