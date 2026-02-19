package com.smbc.school_library.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RentRequestDto {
    @NotNull
    private Long memberId;
    @NotNull
    private Long catalogId;
    @NotNull
    private LocalDate rentDate;
    @NotNull
    private LocalDate dueDate;
}
