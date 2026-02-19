package com.smbc.school_library.controller;

import com.smbc.school_library.dto.ApiResponse;
import com.smbc.school_library.dto.request.PageRequestDto;
import com.smbc.school_library.dto.request.RentRequestDto;
import com.smbc.school_library.dto.response.CatalogResponseDto;
import com.smbc.school_library.dto.response.RentResponseDto;
import com.smbc.school_library.service.RentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rents")
@RequiredArgsConstructor
public class RentController {
    private final RentService rentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RentResponseDto>>> getRents(@Valid @ModelAttribute PageRequestDto request) {
        return ResponseEntity.ok(rentService.getRents(request));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RentResponseDto>> rentBook(@Valid @RequestBody RentRequestDto request) {
        return ResponseEntity.ok(rentService.rentBook(request));
    }

    @PutMapping("/{rentId}/return")
    public ResponseEntity<ApiResponse<RentResponseDto>> returnBook(@PathVariable Long rentId) {
        return ResponseEntity.ok(rentService.returnBook(rentId));
    }
}
