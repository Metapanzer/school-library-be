package com.smbc.school_library.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDto {
    @NotNull
    private Integer page;
    @NotNull
    private Integer pageSize;
}
