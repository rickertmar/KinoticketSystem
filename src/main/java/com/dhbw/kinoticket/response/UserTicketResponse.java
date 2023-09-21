package com.dhbw.kinoticket.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTicketResponse {
    @JsonProperty("price")
    private double price;
    @JsonProperty("booked")
    private boolean isBooked;
    @JsonProperty("discounted")
    private boolean isDiscounted;
    @JsonProperty("valid")
    private boolean isValid;
}
