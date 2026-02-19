package com.smbc.school_library.util;

import com.smbc.school_library.dto.PaginationDto;
import com.smbc.school_library.dto.request.PageRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PageUtil {

    public static Pageable constructPageable(PageRequestDto pageRequest) {
        int pageNumber = pageRequest.getPage() <= 0 ? 1 : pageRequest.getPage();
        int pageSize = pageRequest.getPageSize() <= 0 ? 10 : pageRequest.getPageSize();
        return PageRequest.of(pageNumber - 1, pageSize);
    }

    public static PaginationDto constructPageableResponse(Pageable pageable, Page page) {
        if (pageable != null && !pageable.isUnpaged()) {
            int currentPage = pageable.getPageNumber() + 1;
            int totalPages = page == null ? 0 : page.getTotalPages();
            if (totalPages == 0 && (page == null ? 0L : page.getTotalElements()) > 0L) {
                totalPages = (new BigDecimal(page.getTotalElements())).divide(new BigDecimal(pageable.getPageSize()), RoundingMode.DOWN).intValue();
                currentPage = 1;
            }

            return PaginationDto.builder().currentPage(currentPage).pageSize(pageable.getPageSize()).totalElements((int)(page == null ? 0L : page.getTotalElements())).totalPages(totalPages).build();
        } else {
            int totalElements = page == null ? 0 : (int)page.getTotalElements();
            int totalPages = totalElements == 0 ? 0 : 1;
            return PaginationDto.builder().currentPage(totalElements == 0 ? 0 : 1).pageSize(totalElements).totalElements(totalElements).totalPages(totalPages).build();
        }
    }
}
