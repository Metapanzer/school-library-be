package com.smbc.school_library.service;

import com.smbc.school_library.dto.ApiResponse;
import com.smbc.school_library.dto.request.LoginMemberDto;
import com.smbc.school_library.dto.request.RegisterMemberDto;
import com.smbc.school_library.model.entity.Member;
import com.smbc.school_library.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterMemberDto registerRequest;
    private LoginMemberDto loginRequest;
    private Member existingMember;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterMemberDto.builder()
                .email("member1@mail.com")
                .password("member1")
                .fullName("Member Satu")
                .build();

        loginRequest = LoginMemberDto.builder()
                .email("member1@mail.com")
                .password("member1")
                .build();

        existingMember = Member.builder()
                .email("member1@mail.com")
                .password("encodedPassword")
                .fullName("Member Satu")
                .isActive(true)
                .isDeleted(false)
                .build();
    }

    @Test
    @DisplayName("signup - should register member successfully when email is not in use")
    void signup_ShouldReturnCreated_WhenEmailIsNotInUse() {
        when(memberRepository.findByEmail(registerRequest.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword()))
                .thenReturn("encodedPassword");

        ApiResponse<String> response = authService.signup(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getMessage()).isEqualTo("Member registered successfully");

        verify(memberRepository).save(argThat(member ->
                member.getEmail().equals(registerRequest.getEmail()) &&
                        member.getPassword().equals("encodedPassword") &&
                        member.getFullName().equals(registerRequest.getFullName()) &&
                        member.getIsActive() &&
                        !member.getIsDeleted()
        ));
    }

    @Test
    @DisplayName("signup - should return error when email is already in use")
    void signup_ShouldReturnBadRequest_WhenEmailIsAlreadyInUse() {
        when(memberRepository.findByEmail(registerRequest.getEmail()))
                .thenReturn(Optional.of(existingMember));

        ApiResponse<String> response = authService.signup(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(response.getMessage()).isEqualTo("Email already in use");

        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("signup - should encode password before saving")
    void signup_ShouldEncodePassword_BeforeSaving() {
        when(memberRepository.findByEmail(registerRequest.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("member1"))
                .thenReturn("$2a$10$hashedPasswordMember1");

        authService.signup(registerRequest);

        verify(passwordEncoder).encode("member1");
        verify(memberRepository).save(argThat(member ->
                member.getPassword().equals("$2a$10$hashedPasswordMember1")
        ));
    }

    @Test
    @DisplayName("signup - should save member with isActive=true and isDeleted=false")
    void signup_ShouldSaveMemberWithCorrectFlags() {
        when(memberRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");

        authService.signup(registerRequest);

        verify(memberRepository).save(argThat(member ->
                Boolean.TRUE.equals(member.getIsActive()) &&
                        Boolean.FALSE.equals(member.getIsDeleted())
        ));
    }

    @Test
    @DisplayName("authenticate - should return member when credentials are valid")
    void authenticate_ShouldReturnMember_WhenCredentialsAreValid() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(memberRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(existingMember));

        Member result = authService.authenticate(loginRequest);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(loginRequest.getEmail());
        assertThat(result.getFullName()).isEqualTo("Member Satu");
    }

    @Test
    @DisplayName("authenticate - should call AuthenticationManager with correct token")
    void authenticate_ShouldCallAuthenticationManager_WithCorrectToken() {
        when(memberRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(existingMember));

        authService.authenticate(loginRequest);

        verify(authenticationManager).authenticate(argThat(token ->
                token instanceof UsernamePasswordAuthenticationToken &&
                        token.getPrincipal().equals(loginRequest.getEmail()) &&
                        token.getCredentials().equals(loginRequest.getPassword())
        ));
    }

    @Test
    @DisplayName("authenticate - should throw exception when credentials are invalid")
    void authenticate_ShouldThrowException_WhenCredentialsAreInvalid() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.authenticate(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials");

        verify(memberRepository, never()).findByEmail(any());
    }

    @Test
    @DisplayName("authenticate - should throw NoSuchElementException when member not found after authentication")
    void authenticate_ShouldThrowException_WhenMemberNotFoundAfterAuth() {
        when(authenticationManager.authenticate(any()))
                .thenReturn(null);
        when(memberRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.authenticate(loginRequest))
                .isInstanceOf(NoSuchElementException.class);
    }
}