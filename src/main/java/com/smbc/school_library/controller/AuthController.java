package com.smbc.school_library.controller;

import com.smbc.school_library.dto.ApiResponse;
import com.smbc.school_library.dto.request.LoginMemberDto;
import com.smbc.school_library.dto.request.RegisterMemberDto;
import com.smbc.school_library.dto.response.LoginResponseDto;
import com.smbc.school_library.model.entity.Member;
import com.smbc.school_library.service.AuthService;
import com.smbc.school_library.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterMemberDto request){
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginMemberDto request){
        Member authenticatedUser = authService.authenticate(request);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponseDto loginResponse = new LoginResponseDto(jwtToken);

        return ResponseEntity.ok(ApiResponse.success(loginResponse, "Login successful"));
    }
}
