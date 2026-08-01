package com.breakingchains.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    @Builder.Default
    private String status = "error";
    private String code;
    private String message;
    private Object details;

    public static ApiErrorResponse of(String code, String message) {
        return ApiErrorResponse.builder()
                .status("error")
                .code(code)
                .message(message)
                .build();
    }

    public static ApiErrorResponse of(String code, String message, Object details) {
        return ApiErrorResponse.builder()
                .status("error")
                .code(code)
                .message(message)
                .details(details)
                .build();
    }
}
