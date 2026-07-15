package com.group6.mvc.fpt_cinema.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.entity.Showtime;
import com.group6.mvc.fpt_cinema.repository.ShowtimeRepository;
import com.group6.mvc.fpt_cinema.service.ShowtimeService;
import com.group6.mvc.fpt_cinema.service.impl.ShowtimeServiceImpl;

import jakarta.transaction.Transactional;

//chay 1 lan khi server khoi dong lai
@Component
public class AppStartupRunner implements CommandLineRunner {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private ShowtimeService showtimeService;

    @Override
    @Transactional
    public void run(String... args) {
        LocalDateTime now = LocalDateTime.now();

        // cap nhat bu các suat chieu da ket thuc trong qua khu nhưng chua dc danh finished
        int missed = showtimeRepository.updateMissedShowtimes(now);
        if (missed > 0) {
            System.out.println("[Startup] Đã cập nhật bù " + missed + " suất chiếu thành FINISHED.");
        }

        // lay cac suat chieu tuong lai de dang ký lai lich dong
        List<Showtime> futureShowtimes = showtimeRepository.findFutureActiveShowtimes(now);
        for (Showtime s : futureShowtimes) {
            showtimeService.scheduleShowtimeEndJob(s);
        }
        System.out.println("[Startup] Đã đăng ký lại lịch động cho " + futureShowtimes.size() + " suất chiếu tương lai.");
    }
}
