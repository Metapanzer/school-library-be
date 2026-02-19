package com.smbc.school_library.controller;

import com.smbc.school_library.dto.ApiResponse;
import com.smbc.school_library.dto.request.AddCatalogRequestDto;
import com.smbc.school_library.dto.request.EditCatalogRequestDto;
import com.smbc.school_library.dto.request.PageRequestDto;
import com.smbc.school_library.dto.response.CatalogResponseDto;
import com.smbc.school_library.service.CatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalogs")
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogService catalogService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CatalogResponseDto>>> getCatalogs(@Valid @ModelAttribute PageRequestDto pageRequest) {
        return ResponseEntity.ok(catalogService.getCatalogs(pageRequest));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CatalogResponseDto>> addCatalog(@Valid @RequestBody AddCatalogRequestDto request) {
        return ResponseEntity.ok(catalogService.addCatalog(request));
    }

    @DeleteMapping("/{catalogId}")
    public ResponseEntity<ApiResponse<String>> deleteCatalog(@PathVariable Long catalogId) {
        return ResponseEntity.ok(catalogService.deleteCatalog(catalogId));
    }

    @PutMapping("/{catalogId}")
    public ResponseEntity<ApiResponse<CatalogResponseDto>> editCatalog(@PathVariable Long catalogId, @Valid @RequestBody EditCatalogRequestDto request) {
        return ResponseEntity.ok(catalogService.editCatalog(catalogId, request));
    }
}
