package com.dhbw.kinoticket.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationResponse {
    private String street;
    private String city;
    private String country;
    private String postcode;
}
