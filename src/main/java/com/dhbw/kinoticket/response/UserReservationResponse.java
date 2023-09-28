package com.dhbw.kinoticket.response;

import com.dhbw.kinoticket.entity.Ticket;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class UserReservationResponse {

    @JsonProperty("total")
    private double total;

    @JsonProperty("booked_by")
    private String userName;

    @JsonProperty("showing_start")
    private LocalDateTime showingStart;

    @JsonProperty("showing_extras")
    private String showingExtras;

    @JsonProperty("movie_title")
    private String movieTitle;

    @JsonProperty("tickets")
    private List<Ticket> tickets;

}
