package com.dhbw.kinoticket.request;

import com.dhbw.kinoticket.entity.Discount;
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
    private List<Discount> discountList;
    private boolean isPaid;
    private Long showingId;

}
