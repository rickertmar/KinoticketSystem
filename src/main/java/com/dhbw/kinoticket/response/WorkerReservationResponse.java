package com.dhbw.kinoticket.response;

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
public class WorkerReservationResponse {
    @JsonProperty("id")
    private long id;
    @JsonProperty("total")
    private double total;
    @JsonProperty("paid")
    private boolean isPaid;
    @JsonProperty("booked_by")
    private String userName;
    @JsonProperty("movie_start")
    private LocalDateTime movieStart;
    @JsonProperty("movie_name")
    private String movieName;
    @JsonProperty("tickets")
    private List<UserTicketResponse> tickets;

}

