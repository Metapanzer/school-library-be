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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private MemberResponseDto memberResponseDto;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .email("member1@mail.com")
                .fullName("Member Satu")
                .isActive(true)
                .isDeleted(false)
                .build();

        memberResponseDto = MemberResponseDto.builder()
                .id(1L)
                .email("member1@mail.com")
                .fullName("Member Satu")
                .build();
    }

    @Nested
    @DisplayName("getMembers()")
    class GetMembers {

        private PageRequestDto pageRequestDto;

        @BeforeEach
        void setUp() {
            pageRequestDto = PageRequestDto.builder()
                    .page(0)
                    .pageSize(10)
                    .build();
        }

        @Test
        @DisplayName("should return paginated member list successfully")
        void shouldReturnPaginatedMemberList() {
            List<Member> members = List.of(member);
            Page<Member> memberPage = new PageImpl<>(members);
            List<MemberResponseDto> memberDtos = List.of(memberResponseDto);

            when(memberRepository.findAll(any(Pageable.class))).thenReturn(memberPage);
            when(memberMapper.mapToDto(memberPage)).thenReturn(memberDtos);

            try (MockedStatic<PageUtil> pageUtilMock = mockStatic(PageUtil.class)) {
                pageUtilMock.when(() -> PageUtil.constructPageableResponse(any(), any()))
                        .thenReturn(null);

                ApiResponse<List<MemberResponseDto>> response = memberService.getMembers(pageRequestDto);

                assertThat(response).isNotNull();
                assertThat(response.getStatus()).isEqualTo("success");
                assertThat(response.getMessage()).isEqualTo("Members retrieved successfully");
                assertThat(response.getData()).isEqualTo(memberDtos);
            }
        }

        @Test
        @DisplayName("should build Pageable from PageRequestDto page and pageSize")
        void shouldBuildPageableFromRequest() {
            pageRequestDto = PageRequestDto.builder().page(2).pageSize(5).build();
            Page<Member> memberPage = new PageImpl<>(List.of());

            when(memberRepository.findAll(any(Pageable.class))).thenReturn(memberPage);
            when(memberMapper.mapToDto(memberPage)).thenReturn(List.of());

            try (MockedStatic<PageUtil> pageUtilMock = mockStatic(PageUtil.class)) {
                pageUtilMock.when(() -> PageUtil.constructPageableResponse(any(), any()))
                        .thenReturn(null);

                memberService.getMembers(pageRequestDto);

                verify(memberRepository).findAll(argThat((Pageable pageable) ->
                        pageable.getPageNumber() == 2 && pageable.getPageSize() == 5
                ));
            }
        }

        @Test
        @DisplayName("should return empty list when no members exist")
        void shouldReturnEmptyList_WhenNoMembersExist() {
            Page<Member> emptyPage = new PageImpl<>(List.of());

            when(memberRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);
            when(memberMapper.mapToDto(emptyPage)).thenReturn(List.of());

            try (MockedStatic<PageUtil> pageUtilMock = mockStatic(PageUtil.class)) {
                pageUtilMock.when(() -> PageUtil.constructPageableResponse(any(), any()))
                        .thenReturn(null);

                ApiResponse<List<MemberResponseDto>> response = memberService.getMembers(pageRequestDto);

                assertThat(response.getData()).isEmpty();
            }
        }

        @Test
        @DisplayName("should delegate mapping to MemberMapper")
        void shouldDelegateMappingToMemberMapper() {
            Page<Member> memberPage = new PageImpl<>(List.of(member));

            when(memberRepository.findAll(any(Pageable.class))).thenReturn(memberPage);
            when(memberMapper.mapToDto(memberPage)).thenReturn(List.of(memberResponseDto));

            try (MockedStatic<PageUtil> pageUtilMock = mockStatic(PageUtil.class)) {
                pageUtilMock.when(() -> PageUtil.constructPageableResponse(any(), any()))
                        .thenReturn(null);

                memberService.getMembers(pageRequestDto);

                verify(memberMapper).mapToDto(memberPage);
            }
        }
    }

    @Nested
    @DisplayName("deleteMember()")
    class DeleteMember {

        @Test
        @DisplayName("should soft-delete member and return success response")
        void shouldSoftDeleteMember_AndReturnSuccess() {
            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

            ApiResponse<String> response = memberService.deleteMember(1L);

            assertThat(response).isNotNull();
            assertThat(response.getStatus()).isEqualTo("success");
            assertThat(response.getMessage()).isEqualTo("Member deleted successfully");
            assertThat(member.getIsDeleted()).isTrue();

            verify(memberRepository).save(member);
        }

        @Test
        @DisplayName("should set isDeleted to true on the member entity")
        void shouldSetIsDeletedTrue_OnMemberEntity() {
            member.setIsDeleted(false);
            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

            memberService.deleteMember(1L);

            verify(memberRepository).save(argThat(saved -> Boolean.TRUE.equals(saved.getIsDeleted())));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when member does not exist")
        void shouldThrowResourceNotFoundException_WhenMemberNotFound() {
            when(memberRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> memberService.deleteMember(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Member not found with id: 99");

            verify(memberRepository, never()).save(any());
        }

        @Test
        @DisplayName("should not call save when member is not found")
        void shouldNotCallSave_WhenMemberNotFound() {
            when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> memberService.deleteMember(1L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(memberRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("editMember()")
    class EditMember {

        private EditMemberRequestDto editRequest;

        @BeforeEach
        void setUp() {
            editRequest = EditMemberRequestDto.builder()
                    .fullName("Jane Doe")
                    .build();
        }

        @Test
        @DisplayName("should update member name and return updated member")
        void shouldUpdateMemberName_AndReturnUpdatedMember() {
            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            when(memberMapper.mapToDto(member)).thenReturn(memberResponseDto);

            ApiResponse<MemberResponseDto> response = memberService.editMember(1L, editRequest);

            assertThat(response).isNotNull();
            assertThat(response.getStatus()).isEqualTo("success");
            assertThat(response.getMessage()).isEqualTo("Member updated successfully");
            assertThat(member.getFullName()).isEqualTo("Jane Doe");

            verify(memberRepository).save(member);
        }

        @Test
        @DisplayName("should not update name when request name is null")
        void shouldNotUpdateName_WhenRequestNameIsNull() {
            editRequest = EditMemberRequestDto.builder().fullName(null).build();
            String originalName = member.getFullName();

            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            when(memberMapper.mapToDto(member)).thenReturn(memberResponseDto);

            memberService.editMember(1L, editRequest);

            assertThat(member.getFullName()).isEqualTo(originalName);
            verify(memberRepository).save(member);
        }

        @Test
        @DisplayName("should still save member even when no fields are changed")
        void shouldStillSaveMember_EvenWhenNoFieldsChanged() {
            editRequest = EditMemberRequestDto.builder().fullName(null).build();

            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            when(memberMapper.mapToDto(member)).thenReturn(memberResponseDto);

            memberService.editMember(1L, editRequest);

            verify(memberRepository).save(member);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when member does not exist")
        void shouldThrowResourceNotFoundException_WhenMemberNotFound() {
            when(memberRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> memberService.editMember(99L, editRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Member not found with id: 99");

            verify(memberRepository, never()).save(any());
        }

        @Test
        @DisplayName("should delegate response mapping to MemberMapper")
        void shouldDelegateMappingToMemberMapper() {
            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            when(memberMapper.mapToDto(member)).thenReturn(memberResponseDto);

            ApiResponse<MemberResponseDto> response = memberService.editMember(1L, editRequest);

            verify(memberMapper).mapToDto(member);
            assertThat(response.getData()).isEqualTo(memberResponseDto);
        }
    }
}