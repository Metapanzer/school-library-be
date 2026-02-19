package com.smbc.school_library.service;

import com.smbc.school_library.dto.ApiResponse;
import com.smbc.school_library.dto.request.LoginMemberDto;
import com.smbc.school_library.dto.request.RegisterMemberDto;
import com.smbc.school_library.dto.response.LoginResponseDto;
import com.smbc.school_library.exception.ResourceNotFoundException;
import com.smbc.school_library.model.entity.Member;
import com.smbc.school_library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public ApiResponse<String> signup(RegisterMemberDto request) {
        Optional<Member> optionalMember = memberRepository.findByEmail(request.getEmail());
        if (optionalMember.isPresent()) {
            return ApiResponse.error("Email already in use", HttpStatus.BAD_REQUEST);
        }
        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .isActive(true)
                .isDeleted(false)
                .build();

        memberRepository.save(member);
        return ApiResponse.created("Member registered successfully");
    }

    public Member authenticate(LoginMemberDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        return memberRepository.findByEmail(request.getEmail())
                .orElseThrow();
    }
}
