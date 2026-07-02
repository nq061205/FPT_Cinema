package com.group6.mvc.fpt_cinema.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.group6.mvc.fpt_cinema.entity.Movie;
import com.group6.mvc.fpt_cinema.entity.Room;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewShowTimeListResponse {
    private Integer movieId;
    private String movieTitle;
    private Integer roomId;
    private String roomName;
    private LocalDateTime startTime;
    private BigDecimal basePrice;
    private String status;
}
