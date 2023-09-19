package com.dhbw.kinoticket.response;

import com.dhbw.kinoticket.entity.FSK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponse {

    private Long id;
    private String title;
    private FSK fsk;
    private String description;
    private int releaseYear;
    private String genres;
    private String director;
    private int runningWeek;
    private String runtime;
    private String releaseCountry;
    private String imageSrc;
    private String actors;

}
