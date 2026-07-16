package com.group6.mvc.fpt_cinema.service;

import java.time.LocalDate;
import java.util.List;

import com.group6.mvc.fpt_cinema.dto.request.BatchShowtimeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.group6.mvc.fpt_cinema.dto.request.ShowtimeRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewShowTimeListRequest;
import com.group6.mvc.fpt_cinema.dto.response.ShowtimeResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewShowTimeListResponse;
import com.group6.mvc.fpt_cinema.entity.Showtime;

public interface ShowtimeService extends CrudService<Showtime, Integer> {

    ShowtimeResponse createShowtime(ShowtimeRequest request);

    ShowtimeResponse updateShowtime(Integer id, ShowtimeRequest request);

    void cancelShowtime(Integer id);

    Page<ShowtimeResponse> getAllShowtimes(Integer movieId, Integer roomId, LocalDate date,String status,  Pageable pageable);

    ShowtimeResponse getShowtimeById(Integer id);

    List<ViewShowTimeListResponse> getShowTimesList(ViewShowTimeListRequest request);

    List<ShowtimeResponse> createBatch(BatchShowtimeRequest request);

    List<LocalDate> suggestSlots(Integer movieId, Integer roomId, LocalDate date);

    void scheduleShowtimeEndJob(Showtime showtime);
}
