package com.smbc.school_library.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
