package com.smbc.school_library.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Data
@Builder
public class ApiResponse<T> {
    private String status;
    private int code;
    private String message;
    private T data;
    private PaginationDto pagination;

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, HttpStatusCode statusCode) {
        return ApiResponse.<T>builder()
                .status("error")
                .code(statusCode.value())
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> created(String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .code(HttpStatus.CREATED.value())
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> paginatedSuccess(T data, PaginationDto pagination, String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .pagination(pagination)
                .build();
    }
}
