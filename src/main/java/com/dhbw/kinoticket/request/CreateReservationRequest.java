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
public class CreateReservationRequest {

    private List<Long> selectedSeatIdList;
    private int studentDiscounts;
    private int childDiscounts;
    private int noDiscounts;
    private Long showingId;

}
