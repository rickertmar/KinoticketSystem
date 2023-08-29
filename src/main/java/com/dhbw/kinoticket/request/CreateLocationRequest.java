package com.dhbw.kinoticket.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLocationRequest {
    private String street;
    private String city;
    private String country;
    private String postcode;
}