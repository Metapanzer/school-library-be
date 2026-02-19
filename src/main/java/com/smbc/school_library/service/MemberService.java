package com.smbc.school_library.service;

import com.smbc.school_library.dto.ApiResponse;
import com.smbc.school_library.dto.request.EditMemberRequestDto;
import com.smbc.school_library.dto.request.PageRequestDto;
import com.smbc.school_library.dto.response.MemberResponseDto;
import com.smbc.school_library.exception.ResourceNotFoundException;
import com.smbc.school_library.model.entity.Member;
import com.smbc.school_library.repository.MemberRepository;
import com.smbc.school_library.util.MemberMapper;
import com.smbc.school_library.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public ApiResponse<List<MemberResponseDto>> getMembers(PageRequestDto pageRequestDto) {
        Pageable pageable = Pageable.ofSize(pageRequestDto.getPageSize()).withPage(pageRequestDto.getPage());
        Page<Member> memberPage = memberRepository.findAll(pageable);

        return ApiResponse.paginatedSuccess(memberMapper.mapToDto(memberPage), PageUtil.constructPageableResponse(pageable, memberPage), "Members retrieved successfully");
    }

    public ApiResponse<String> deleteMember(Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            throw new ResourceNotFoundException("Member not found with id: " + memberId);
        }
        member.get().setIsDeleted(true);
        memberRepository.save(member.get());

        return ApiResponse.success(null,"Member deleted successfully");
    }

    public ApiResponse<MemberResponseDto> editMember(Long memberId, EditMemberRequestDto request) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        if (request.getName() != null) {
            member.setFullName(request.getName());
        }

        memberRepository.save(member);
        return ApiResponse.success(memberMapper.mapToDto(member), "Member updated successfully");
    }
}
