package com.smbc.school_library.service;

import com.smbc.school_library.dto.ApiResponse;
import com.smbc.school_library.dto.request.AddCatalogRequestDto;
import com.smbc.school_library.dto.request.EditCatalogRequestDto;
import com.smbc.school_library.dto.request.PageRequestDto;
import com.smbc.school_library.dto.response.CatalogResponseDto;
import com.smbc.school_library.exception.ResourceNotFoundException;
import com.smbc.school_library.model.entity.Catalog;
import com.smbc.school_library.repository.CatalogRepository;
import com.smbc.school_library.util.CatalogMapper;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private CatalogRepository catalogRepository;

    @Mock
    private CatalogMapper catalogMapper;

    @InjectMocks
    private CatalogService catalogService;

    private Catalog catalog;
    private CatalogResponseDto catalogResponseDto;

    @BeforeEach
    void setUp() {
        catalog = Catalog.builder()
                .id(1L)
                .title("Buku Satu")
                .author("Author Satu")
                .publisher("Publisher Satu")
                .isbn("978-0132350884")
                .totalQty(5)
                .availableQty(5)
                .isDeleted(false)
                .build();

        catalogResponseDto = CatalogResponseDto.builder()
                .id(1L)
                .title("Buku Satu")
                .author("Author Satu")
                .publisher("Publisher Satu")
                .isbn("978-0132350884")
                .totalQty(5)
                .availableQty(5)
                .build();
    }

    @Nested
    @DisplayName("getCatalogs()")
    class GetCatalogs {

        private PageRequestDto pageRequestDto;

        @BeforeEach
        void setUp() {
            pageRequestDto = PageRequestDto.builder()
                    .page(0)
                    .pageSize(10)
                    .build();
        }

        @Test
        @DisplayName("should return paginated catalog list with success response")
        void shouldReturnPaginatedCatalogList() {
            Page<Catalog> catalogPage = new PageImpl<>(List.of(catalog));
            List<CatalogResponseDto> catalogDtos = List.of(catalogResponseDto);

            when(catalogRepository.findAll(any(Pageable.class))).thenReturn(catalogPage);
            when(catalogMapper.mapToDto(catalogPage)).thenReturn(catalogDtos);

            try (MockedStatic<PageUtil> pageUtilMock = mockStatic(PageUtil.class)) {
                pageUtilMock.when(() -> PageUtil.constructPageableResponse(any(), any()))
                        .thenReturn(null);

                ApiResponse<List<CatalogResponseDto>> response = catalogService.getCatalogs(pageRequestDto);

                assertThat(response).isNotNull();
                assertThat(response.getStatus()).isEqualTo("success");
                assertThat(response.getMessage()).isEqualTo("Catalogs retrieved successfully");
                assertThat(response.getData()).isEqualTo(catalogDtos);
            }
        }

        @Test
        @DisplayName("should build Pageable with correct page number and page size")
        void shouldBuildPageable_WithCorrectPageNumberAndSize() {
            pageRequestDto = PageRequestDto.builder().page(3).pageSize(15).build();
            Page<Catalog> catalogPage = new PageImpl<>(List.of());

            when(catalogRepository.findAll(any(Pageable.class))).thenReturn(catalogPage);
            when(catalogMapper.mapToDto(catalogPage)).thenReturn(List.of());

            try (MockedStatic<PageUtil> pageUtilMock = mockStatic(PageUtil.class)) {
                pageUtilMock.when(() -> PageUtil.constructPageableResponse(any(), any()))
                        .thenReturn(null);

                catalogService.getCatalogs(pageRequestDto);

                verify(catalogRepository).findAll(argThat((Pageable pageable) ->
                        pageable.getPageNumber() == 3 && pageable.getPageSize() == 15
                ));
            }
        }

        @Test
        @DisplayName("should return empty list when no catalogs exist")
        void shouldReturnEmptyList_WhenNoCatalogsExist() {
            Page<Catalog> emptyPage = new PageImpl<>(List.of());

            when(catalogRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);
            when(catalogMapper.mapToDto(emptyPage)).thenReturn(List.of());

            try (MockedStatic<PageUtil> pageUtilMock = mockStatic(PageUtil.class)) {
                pageUtilMock.when(() -> PageUtil.constructPageableResponse(any(), any()))
                        .thenReturn(null);

                ApiResponse<List<CatalogResponseDto>> response = catalogService.getCatalogs(pageRequestDto);

                assertThat(response.getData()).isEmpty();
            }
        }

        @Test
        @DisplayName("should delegate mapping to CatalogMapper")
        void shouldDelegateMappingToCatalogMapper() {
            Page<Catalog> catalogPage = new PageImpl<>(List.of(catalog));

            when(catalogRepository.findAll(any(Pageable.class))).thenReturn(catalogPage);
            when(catalogMapper.mapToDto(catalogPage)).thenReturn(List.of(catalogResponseDto));

            try (MockedStatic<PageUtil> pageUtilMock = mockStatic(PageUtil.class)) {
                pageUtilMock.when(() -> PageUtil.constructPageableResponse(any(), any()))
                        .thenReturn(null);

                catalogService.getCatalogs(pageRequestDto);

                verify(catalogMapper).mapToDto(catalogPage);
            }
        }
    }

    @Nested
    @DisplayName("addCatalog()")
    class AddCatalog {

        private AddCatalogRequestDto addRequest;

        @BeforeEach
        void setUp() {
            addRequest = AddCatalogRequestDto.builder()
                    .title("Buku Satu")
                    .author("Author Satu")
                    .publisher("Publisher Satu")
                    .isbn("978-0132350884")
                    .totalQty(5)
                    .build();
        }

        @Test
        @DisplayName("should save catalog and return success response")
        void shouldSaveCatalog_AndReturnSuccessResponse() {
            when(catalogRepository.save(any(Catalog.class))).thenReturn(catalog);
            when(catalogMapper.mapToDto(catalog)).thenReturn(catalogResponseDto);

            ApiResponse<CatalogResponseDto> response = catalogService.addCatalog(addRequest);

            assertThat(response).isNotNull();
            assertThat(response.getStatus()).isEqualTo("success");
            assertThat(response.getMessage()).isEqualTo("Catalog added successfully");
            assertThat(response.getData()).isEqualTo(catalogResponseDto);
        }

        @Test
        @DisplayName("should build catalog entity with all fields from request")
        void shouldBuildCatalogEntity_WithAllFieldsFromRequest() {
            when(catalogRepository.save(any(Catalog.class))).thenReturn(catalog);
            when(catalogMapper.mapToDto(catalog)).thenReturn(catalogResponseDto);

            catalogService.addCatalog(addRequest);

            verify(catalogRepository).save(argThat(saved ->
                    saved.getTitle().equals("Buku Satu") &&
                            saved.getAuthor().equals("Author Satu") &&
                            saved.getPublisher().equals("Publisher Satu") &&
                            saved.getIsbn().equals("978-0132350884") &&
                            saved.getTotalQty() == 5
            ));
        }

        @Test
        @DisplayName("should set availableQty equal to totalQty on creation")
        void shouldSetAvailableQty_EqualToTotalQty() {
            addRequest = AddCatalogRequestDto.builder()
                    .title("Buku Dua")
                    .author("Author Dua")
                    .publisher("Publisher Dua")
                    .isbn("978-0135957059")
                    .totalQty(8)
                    .build();

            when(catalogRepository.save(any(Catalog.class))).thenReturn(catalog);
            when(catalogMapper.mapToDto(catalog)).thenReturn(catalogResponseDto);

            catalogService.addCatalog(addRequest);

            verify(catalogRepository).save(argThat(saved ->
                    saved.getTotalQty() == 8 && saved.getAvailableQty() == 8
            ));
        }

        @Test
        @DisplayName("should delegate saved entity mapping to CatalogMapper")
        void shouldDelegateSavedEntityMapping_ToCatalogMapper() {
            when(catalogRepository.save(any(Catalog.class))).thenReturn(catalog);
            when(catalogMapper.mapToDto(catalog)).thenReturn(catalogResponseDto);

            catalogService.addCatalog(addRequest);

            verify(catalogMapper).mapToDto(catalog);
        }
    }

    @Nested
    @DisplayName("editCatalog()")
    class EditCatalog {

        private EditCatalogRequestDto editRequest;

        @BeforeEach
        void setUp() {
            editRequest = EditCatalogRequestDto.builder()
                    .title("Buku Tiga")
                    .author("Author Tiga")
                    .publisher("Publisher Tiga")
                    .isbn("978-0134494166")
                    .totalQty(10)
                    .build();
        }

        @Test
        @DisplayName("should update all fields and return updated catalog")
        void shouldUpdateAllFields_AndReturnUpdatedCatalog() {
            when(catalogRepository.findById(1L)).thenReturn(Optional.of(catalog));
            when(catalogRepository.save(catalog)).thenReturn(catalog);
            when(catalogMapper.mapToDto(catalog)).thenReturn(catalogResponseDto);

            ApiResponse<CatalogResponseDto> response = catalogService.editCatalog(1L, editRequest);

            assertThat(response).isNotNull();
            assertThat(response.getStatus()).isEqualTo("success");
            assertThat(response.getMessage()).isEqualTo("Catalog updated successfully");
            assertThat(response.getData()).isEqualTo(catalogResponseDto);
        }

        @Test
        @DisplayName("should apply all fields from request to the existing catalog entity")
        void shouldApplyAllRequestFields_ToExistingCatalogEntity() {
            when(catalogRepository.findById(1L)).thenReturn(Optional.of(catalog));
            when(catalogRepository.save(catalog)).thenReturn(catalog);
            when(catalogMapper.mapToDto(catalog)).thenReturn(catalogResponseDto);

            catalogService.editCatalog(1L, editRequest);

            assertThat(catalog.getTitle()).isEqualTo("Buku Tiga");
            assertThat(catalog.getAuthor()).isEqualTo("Author Tiga");
            assertThat(catalog.getPublisher()).isEqualTo("Publisher Tiga");
            assertThat(catalog.getIsbn()).isEqualTo("978-0134494166");
            assertThat(catalog.getTotalQty()).isEqualTo(10);
        }

        @Test
        @DisplayName("should set availableQty equal to totalQty on edit")
        void shouldSetAvailableQty_EqualToTotalQty_OnEdit() {
            editRequest = EditCatalogRequestDto.builder()
                    .title("Test Book")
                    .author("Author")
                    .publisher("Publisher")
                    .isbn("123-456")
                    .totalQty(7)
                    .build();

            when(catalogRepository.findById(1L)).thenReturn(Optional.of(catalog));
            when(catalogRepository.save(catalog)).thenReturn(catalog);
            when(catalogMapper.mapToDto(catalog)).thenReturn(catalogResponseDto);

            catalogService.editCatalog(1L, editRequest);

            verify(catalogRepository).save(argThat(saved ->
                    saved.getTotalQty() == 7 && saved.getAvailableQty() == 7
            ));
        }

        @Test
        @DisplayName("should delegate updated entity mapping to CatalogMapper")
        void shouldDelegateUpdatedEntityMapping_ToCatalogMapper() {
            when(catalogRepository.findById(1L)).thenReturn(Optional.of(catalog));
            when(catalogRepository.save(catalog)).thenReturn(catalog);
            when(catalogMapper.mapToDto(catalog)).thenReturn(catalogResponseDto);

            catalogService.editCatalog(1L, editRequest);

            verify(catalogMapper).mapToDto(catalog);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when catalog not found")
        void shouldThrowResourceNotFoundException_WhenCatalogNotFound() {
            when(catalogRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> catalogService.editCatalog(99L, editRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Catalog not found with id: 99");

            verify(catalogRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteCatalog()")
    class DeleteCatalog {

        @Test
        @DisplayName("should soft-delete catalog and return success response")
        void shouldSoftDeleteCatalog_AndReturnSuccessResponse() {
            when(catalogRepository.findById(1L)).thenReturn(Optional.of(catalog));

            ApiResponse<String> response = catalogService.deleteCatalog(1L);

            assertThat(response).isNotNull();
            assertThat(response.getStatus()).isEqualTo("success");
            assertThat(response.getMessage()).isEqualTo("Catalog deleted successfully");
            assertThat(catalog.getIsDeleted()).isTrue();

            verify(catalogRepository).save(catalog);
        }

        @Test
        @DisplayName("should set isDeleted to true on the catalog entity")
        void shouldSetIsDeletedTrue_OnCatalogEntity() {
            catalog.setIsDeleted(false);
            when(catalogRepository.findById(1L)).thenReturn(Optional.of(catalog));

            catalogService.deleteCatalog(1L);

            verify(catalogRepository).save(argThat(saved ->
                    Boolean.TRUE.equals(saved.getIsDeleted())
            ));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when catalog not found")
        void shouldThrowResourceNotFoundException_WhenCatalogNotFound() {
            when(catalogRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> catalogService.deleteCatalog(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Catalog not found with id: 99");

            verify(catalogRepository, never()).save(any());
        }

        @Test
        @DisplayName("should not call save when catalog is not found")
        void shouldNotCallSave_WhenCatalogNotFound() {
            when(catalogRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> catalogService.deleteCatalog(1L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(catalogRepository, never()).save(any());
        }

        @Test
        @DisplayName("should return null data in success response")
        void shouldReturnNullData_InSuccessResponse() {
            when(catalogRepository.findById(1L)).thenReturn(Optional.of(catalog));

            ApiResponse<String> response = catalogService.deleteCatalog(1L);

            assertThat(response.getData()).isNull();
        }
    }
}