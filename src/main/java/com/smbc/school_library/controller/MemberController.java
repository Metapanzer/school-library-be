package com.smbc.school_library.controller;

import com.smbc.school_library.dto.ApiResponse;
import com.smbc.school_library.dto.request.EditMemberRequestDto;
import com.smbc.school_library.dto.request.PageRequestDto;
import com.smbc.school_library.dto.response.MemberResponseDto;
import com.smbc.school_library.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    public final MemberService memberService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponseDto>>> getMembers(@Valid @ModelAttribute PageRequestDto pageRequest) {
        return ResponseEntity.ok(memberService.getMembers(pageRequest));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<String>> deleteMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.deleteMember(memberId));
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberResponseDto>> editMember(@PathVariable Long memberId, @Valid @RequestBody EditMemberRequestDto request) {
        return ResponseEntity.ok(memberService.editMember(memberId, request));
    }

}
