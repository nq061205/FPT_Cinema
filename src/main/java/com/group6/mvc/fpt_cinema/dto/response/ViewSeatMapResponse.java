package com.group6.mvc.fpt_cinema.dto.response;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewSeatMapResponse {
    private Integer roomId;
    private String roomName;
    private List<ViewSeatResponse> seats;
}
