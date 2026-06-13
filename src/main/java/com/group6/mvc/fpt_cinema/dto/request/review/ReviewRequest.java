package com.group6.mvc.fpt_cinema.dto.request.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private Integer movieId;
    private Integer bookingId;
    private Integer rating;
    private String comment;


}
