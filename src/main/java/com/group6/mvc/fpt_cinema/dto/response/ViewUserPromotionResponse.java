package com.group6.mvc.fpt_cinema.dto.response;
import java.time.LocalDateTime;
import java.util.List;

import com.group6.mvc.fpt_cinema.entity.Promotion;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
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
public class ViewUserPromotionResponse {

    private Integer userPromotionId;

    private String status;

    private LocalDateTime assignedAt;

    private ViewPromotionResponse promotion;
}
