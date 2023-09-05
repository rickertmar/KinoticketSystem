package com.dhbw.kinoticket.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSeatRequest {
    private int seatRow;
    private int number;
    private int xLoc; //relative X-Location in theatre
    private int yLoc; //relative Y-Location in theatre
    private boolean isBlocked;
}
