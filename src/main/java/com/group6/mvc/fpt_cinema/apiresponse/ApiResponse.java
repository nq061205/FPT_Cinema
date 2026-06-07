package com.group6.mvc.fpt_cinema.apiresponse;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ApiResponse<T> {
    @Builder.Default
    int code = 200;
    String message;
    T result;
}   