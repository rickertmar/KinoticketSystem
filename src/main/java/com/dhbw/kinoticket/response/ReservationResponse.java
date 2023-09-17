package com.dhbw.kinoticket.response;

import com.dhbw.kinoticket.entity.Movie;
import com.dhbw.kinoticket.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponse {

    private Movie movie;
    private LocalDateTime time;
    private List<Ticket> tickets;
    private double total;

}
