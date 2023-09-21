package com.dhbw.kinoticket.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateShowingRequest {

    private LocalDateTime time;
    private String showingExtras;
    private Long movieId;
    private Long cinemaHallId;
    private double seatPrice;

}
