package com.smbc.school_library.service;

import com.smbc.school_library.dto.ApiResponse;
import com.smbc.school_library.dto.request.AddCatalogRequestDto;
import com.smbc.school_library.dto.request.EditCatalogRequestDto;
import com.smbc.school_library.dto.request.PageRequestDto;
import com.smbc.school_library.dto.response.CatalogResponseDto;
import com.smbc.school_library.exception.ResourceNotFoundException;
import com.smbc.school_library.model.entity.Catalog;
import com.smbc.school_library.repository.CatalogRepository;
import com.smbc.school_library.util.CatalogMapper;
import com.smbc.school_library.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CatalogService {
    private final CatalogRepository catalogRepository;
    private final CatalogMapper catalogMapper;

    public ApiResponse<List<CatalogResponseDto>> getCatalogs(PageRequestDto request) {
        Pageable pageable = Pageable.ofSize(request.getPageSize()).withPage(request.getPage());
        Page<Catalog> catalogPage = catalogRepository.findAll(pageable);

        List<CatalogResponseDto> catalogs = catalogMapper.mapToDto(catalogPage);
        return ApiResponse.paginatedSuccess(catalogs, PageUtil.constructPageableResponse(pageable, catalogPage), "Catalogs retrieved successfully");
    }

    public ApiResponse<CatalogResponseDto> addCatalog(AddCatalogRequestDto request) {
        Catalog catalog = Catalog.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .isbn(request.getIsbn())
                .totalQty(request.getTotalQty())
                .availableQty(request.getTotalQty())
                .build();
        Catalog savedCatalog = catalogRepository.save(catalog);
        return ApiResponse.success(catalogMapper.mapToDto(savedCatalog), "Catalog added successfully");
    }

    public ApiResponse<CatalogResponseDto> editCatalog(Long catalogId, EditCatalogRequestDto request) {
        Catalog catalog = catalogRepository.findById(catalogId).orElseThrow(()-> new ResourceNotFoundException("Catalog not found with id: " + catalogId));
        catalog.setTitle(request.getTitle());
        catalog.setAuthor(request.getAuthor());
        catalog.setPublisher(request.getPublisher());
        catalog.setIsbn(request.getIsbn());
        catalog.setTotalQty(request.getTotalQty());
        catalog.setAvailableQty(request.getTotalQty());

        Catalog updatedCatalog = catalogRepository.save(catalog);
        return ApiResponse.success(catalogMapper.mapToDto(updatedCatalog), "Catalog updated successfully");
    }

    public ApiResponse<String> deleteCatalog(Long catalogId) {
        Catalog catalog = catalogRepository.findById(catalogId).orElseThrow(()-> new ResourceNotFoundException("Catalog not found with id: " + catalogId));
        catalog.setIsDeleted(true);
        catalogRepository.save(catalog);
        return ApiResponse.success(null, "Catalog deleted successfully");
    }
}
