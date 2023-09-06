package com.dhbw.kinoticket.request;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCinemaRequest {

    private String name;
    private String street;
    private String city;
    private String country;
    private String postcode;

}
