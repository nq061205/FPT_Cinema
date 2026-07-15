
package com.group6.mvc.fpt_cinema.mapper;

import com.group6.mvc.fpt_cinema.dto.request.ReviewRequest;
import com.group6.mvc.fpt_cinema.entity.Booking;
import com.group6.mvc.fpt_cinema.entity.Movie;
import org.mapstruct.Mapper;

import com.group6.mvc.fpt_cinema.dto.response.ReviewResponse;
import com.group6.mvc.fpt_cinema.entity.Review;
import com.group6.mvc.fpt_cinema.entity.User;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface IReviewMapper {

    String DEFAULT_AVATAR = "https://i.pinimg.com/736x/18/3b/59/183b590ac65cf71f947f33c9de8f7bc8.jpg";

    @Mapping(target = "movieId", source = "movie.id")
    @Mapping(target = "maskedName", source = "customer.fullName", qualifiedByName = "mask")
    @Mapping(target = "avatarUrl", constant = DEFAULT_AVATAR)
    @Mapping(target = "movieTitle", source = "movie.title")
    ReviewResponse toResponse(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "movie", source = "movie")
    @Mapping(target = "booking", source = "booking")
    @Mapping(target = "status", constant = "VISIBLE")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review toEntity(ReviewRequest request, User customer, Movie movie, Booking booking);

    @Named("mask")
    static String maskName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "*****";
        }

        if (fullName.length() <= 2) {
            return fullName.charAt(0) + "*****";
        }

        if (fullName.length() <= 4) {
            return fullName.charAt(0) + "*****" + fullName.substring(fullName.length() - 1);
        }

        return fullName.substring(0, 2) + "*****" + fullName.substring(fullName.length() - 2);
    }

}
