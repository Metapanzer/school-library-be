package com.smbc.school_library.util;

import com.smbc.school_library.dto.response.CatalogResponseDto;
import com.smbc.school_library.model.entity.Catalog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CatalogMapper {

    private CatalogResponseDto mapEntityToDto(Catalog entity) {
        return CatalogResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .isbn(entity.getIsbn())
                .publisher(entity.getPublisher())
                .totalQty(entity.getTotalQty())
                .availableQty(entity.getAvailableQty())
                .build();
    }

    public CatalogResponseDto mapToDto(Catalog entity) {
        return mapEntityToDto(entity);
    }

    public List<CatalogResponseDto> mapToDto(Page<Catalog> entities) {
        return entities.stream()
                .map(this::mapEntityToDto)
                .toList();
    }
}
