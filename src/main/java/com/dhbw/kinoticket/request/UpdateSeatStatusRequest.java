package com.dhbw.kinoticket.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSeatStatusRequest {

    private Long showingId;
    private List<Long> seatIds;

}
