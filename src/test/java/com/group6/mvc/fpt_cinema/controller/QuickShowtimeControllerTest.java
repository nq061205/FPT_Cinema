package com.group6.mvc.fpt_cinema.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.response.ShowtimeResponse;
import com.group6.mvc.fpt_cinema.service.ShowtimeService;

@ExtendWith(MockitoExtension.class)
class QuickShowtimeControllerTest {

    @Mock
    private ShowtimeService showtimeService;

    @InjectMocks
    private QuickShowtimeController controller;

    @Test
    void delegatesQuickShowtimeRequestToExistingShowtimeService() {
        PageRequest pageable = PageRequest.of(0, 20);
        LocalDate date = LocalDate.of(2026, 7, 15);
        ShowtimeResponse showtime = ShowtimeResponse.builder().id(1).build();
        Page<ShowtimeResponse> expected = new PageImpl<>(List.of(showtime), pageable, 1);
        when(showtimeService.getAllShowtimes(2, null, date, "OPEN", pageable))
                .thenReturn(expected);

        ApiResponse<Page<ShowtimeResponse>> response = controller.getQuickShowtimes(
                1, 2, null, date, "OPEN", pageable);

        assertThat(response.getResult()).isSameAs(expected);
        verify(showtimeService).getAllShowtimes(2, null, date, "OPEN", pageable);
    }

}
