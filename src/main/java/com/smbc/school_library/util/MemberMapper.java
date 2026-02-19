package com.smbc.school_library.util;

import com.smbc.school_library.dto.response.MemberResponseDto;
import com.smbc.school_library.model.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberMapper {
    private MemberResponseDto mapEntityToDto(Member entity) {
        return MemberResponseDto.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .build();
    };

    public MemberResponseDto mapToDto(Member entity) {
        return mapEntityToDto(entity);
    }

    public List<MemberResponseDto> mapToDto(Page<Member> entities) {
        return entities.stream()
                .map(this::mapEntityToDto)
                .toList();
    }
}
