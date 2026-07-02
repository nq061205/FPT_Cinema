package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.dto.request.EditReviewRequest;
import com.group6.mvc.fpt_cinema.dto.request.ReviewRequest;
import com.group6.mvc.fpt_cinema.dto.response.ReviewResponse;
import com.group6.mvc.fpt_cinema.entity.Booking;
import com.group6.mvc.fpt_cinema.entity.Movie;
import com.group6.mvc.fpt_cinema.entity.Review;
import com.group6.mvc.fpt_cinema.entity.User;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.mapper.IReviewMapper;
import com.group6.mvc.fpt_cinema.repository.BookingRepository;
import com.group6.mvc.fpt_cinema.repository.MovieRepository;
import com.group6.mvc.fpt_cinema.repository.ReviewRepository;
import com.group6.mvc.fpt_cinema.repository.TicketRepository;
import com.group6.mvc.fpt_cinema.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReviewServiceImpl
        extends AbstractCrudService<Review, Integer>
        implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private IReviewMapper IReviewMapper;



    public ReviewServiceImpl(ReviewRepository repository) {
        super(repository);
        this.reviewRepository = repository;
    }

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request, Integer userId) {
        //Validate 1-5 sao
        if(request.getRating() == null || request.getRating() < 1 || request.getRating() > 5){
            throw new AppException(ErrorCode.INVALID_RATING);
        }

        // tim booking theo id
        Booking booking = bookingRepository.findByIdAndCustomerId(request.getBookingId(), userId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

         if(!"CONFIRMED".equals(booking.getStatus())){
             throw new AppException(ErrorCode.BOOKING_NOT_CONFIRMED);
         }

         if(!ticketRepository.existsByBookingIdAndStatus(booking.getId(), "USED")){
             throw new AppException(ErrorCode.TICKET_NOT_USED);
         }

         if(booking.getShowtime().getStartTime().isAfter(LocalDateTime.now())){

            throw new AppException(ErrorCode.SHOWTIME_NOT_PASSED);
         }

         if(!booking.getShowtime().getMovie().getId().equals(request.getMovieId())){
             throw new AppException(ErrorCode.MOVIE_MISMATCH);
         }

         if(reviewRepository.existsByCustomerIdAndBookingId(userId, request.getBookingId())){
             throw new AppException(ErrorCode.ALREADY_REVIEW);
         }

         int reviewCount = reviewRepository.countByCustomerIdAndMovieId(userId, request.getMovieId());

         if(reviewCount >= 2){
             throw new AppException(ErrorCode.REVIEW_LIMIT_ACCESS);
         }

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(()-> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        User customer = booking.getCustomer();

        Review review = IReviewMapper.toEntity(request, customer, movie, booking);
        Review saved = reviewRepository.save(review);


        return IReviewMapper.toResponse(saved);
    }

    @Override
    public Page<ReviewResponse> getReviewsByMovie(Integer movieId, Pageable pageable) {
        return reviewRepository.findByMovieIdAndStatusOrderByCreatedAtDesc(movieId, "VISIBLE", pageable)
        .map(IReviewMapper::toResponse);
    }

    @Override
    @Transactional
    public ReviewResponse editReview(Integer reviewId, EditReviewRequest request, Integer userId) {
        //Tim review
        Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        if(!review.getCustomer().getId().equals(userId)){
            throw new AppException(ErrorCode.NOT_REVIEW_OWNER);
        }

        if(review.getCreatedAt().plusHours(24).isBefore(LocalDateTime.now())){
            throw new AppException(ErrorCode.EDIT_TIME_EXPIRED);
        }

        if(request.getRating() != null){
            review.setRating(request.getRating());
        }

        if(request.getComment() != null){
            review.setComment(request.getComment());
        }

        Review saved = reviewRepository.save(review);
        return IReviewMapper.toResponse(saved);
    }

}
