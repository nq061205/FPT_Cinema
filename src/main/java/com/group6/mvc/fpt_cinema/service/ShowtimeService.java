package com.group6.mvc.fpt_cinema.service;

import java.time.LocalDate;
import java.util.List;

import com.group6.mvc.fpt_cinema.dto.request.showtime.ShowtimeRequest;
import com.group6.mvc.fpt_cinema.dto.response.showtime.ShowtimeResponse;
import com.group6.mvc.fpt_cinema.entity.Showtime;

public interface ShowtimeService extends CrudService<Showtime, Integer> {

    ShowtimeResponse createShowtime(ShowtimeRequest request); 

    ShowtimeResponse updateShowtime(Integer id, ShowtimeRequest request); 

    void cancelShowtime(Integer id); 

    List<ShowtimeResponse> getAllShowtimes(Integer movieId, Integer roomId, LocalDate date); 

    ShowtimeResponse getShowtimeById(Integer id); 
}
