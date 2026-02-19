package com.smbc.school_library.service;

import com.smbc.school_library.dto.ApiResponse;
import com.smbc.school_library.dto.request.PageRequestDto;
import com.smbc.school_library.dto.request.RentRequestDto;
import com.smbc.school_library.dto.response.RentResponseDto;
import com.smbc.school_library.exception.ResourceNotFoundException;
import com.smbc.school_library.model.entity.Catalog;
import com.smbc.school_library.model.entity.Member;
import com.smbc.school_library.model.entity.Rent;
import com.smbc.school_library.repository.CatalogRepository;
import com.smbc.school_library.repository.MemberRepository;
import com.smbc.school_library.repository.RentRepository;
import com.smbc.school_library.util.PageUtil;
import com.smbc.school_library.util.RentMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RentService {
    private final RentRepository rentRepository;
    private final MemberRepository memberRepository;
    private final CatalogRepository catalogRepository;
    private final RentMapper rentMapper;

    public ApiResponse<List<RentResponseDto>> getRents(PageRequestDto request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());
        Page<Rent> rentPage = rentRepository.findAll(pageable);
        List<RentResponseDto> rents = rentMapper.mapToDto(rentPage);
        return ApiResponse.paginatedSuccess(rents, PageUtil.constructPageableResponse(pageable, rentPage), "Rents retrieved successfully");
    }

    @Transactional
    public ApiResponse<RentResponseDto> rentBook(RentRequestDto request) {
        Member member = memberRepository.findById(request.getMemberId()).orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.getMemberId()));
        Catalog catalog = catalogRepository.findById(request.getCatalogId()).orElseThrow(() -> new ResourceNotFoundException("Catalog not found with id: " + request.getCatalogId()));
        if (catalog.getAvailableQty() <= 0) {
            throw new ResourceNotFoundException("No available copies for this book");
        }
        catalog.setAvailableQty(catalog.getAvailableQty() - 1);
        catalogRepository.save(catalog);
        Rent rent = Rent.builder()
                .member(member)
                .catalog(catalog)
                .rentDate(request.getRentDate())
                .dueDate(request.getDueDate())
                .build();


        Rent savedRent = rentRepository.save(rent);
        return ApiResponse.success(rentMapper.mapToDto(savedRent), "Book rented successfully");
    }

    @Transactional
     public ApiResponse<RentResponseDto> returnBook(Long rentId) {
        Rent rent = rentRepository.findById(rentId).orElseThrow(() -> new ResourceNotFoundException("Rent not found with id: " + rentId));
        if (rent.getReturnDate() != null) {
            throw new RuntimeException("Book has already been returned for this rent");
        }
        Catalog catalog = rent.getCatalog();
        catalog.setAvailableQty(catalog.getAvailableQty() + 1);
        catalogRepository.save(catalog);

        rent.setReturnDate(LocalDate.now());
        Rent updatedRent = rentRepository.save(rent);
        return ApiResponse.success(rentMapper.mapToDto(updatedRent), "Book returned successfully");
    }
}
