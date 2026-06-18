package com.group6.mvc.fpt_cinema.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private Integer id;
    private String maskedName;
    private String avatarUrl;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private String movieTitle;
    private Integer movieId;
    private LocalDateTime updatedAt;

}
