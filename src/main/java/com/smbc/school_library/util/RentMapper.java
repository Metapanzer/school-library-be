package com.smbc.school_library.util;

import com.smbc.school_library.dto.response.RentResponseDto;
import com.smbc.school_library.model.entity.Catalog;
import com.smbc.school_library.model.entity.Rent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RentMapper {
    private final MemberMapper memberMapper;
    private final CatalogMapper catalogMapper;

    private RentResponseDto mapEntityToDto(Rent entity) {
        return RentResponseDto.builder()
                .id(entity.getId())
                .member(memberMapper.mapToDto(entity.getMember()))
                .catalog(catalogMapper.mapToDto(entity.getCatalog()))
                .rentDate(entity.getRentDate().toString())
                .dueDate(entity.getDueDate().toString())
                .returnDate(entity.getReturnDate() != null ? entity.getReturnDate().toString() : null)
                .build();
    }

    public RentResponseDto mapToDto(Rent entity) {
        return mapEntityToDto(entity);
    }

    public List<RentResponseDto> mapToDto(Page<Rent> entity) {
        return entity.stream()
                .map(this::mapEntityToDto)
                .toList();
    }
}
