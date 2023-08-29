package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.dao.CreateCinemaRequest;
import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.service.CinemaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/cinemas")
public class CinemaController {
    private final CinemaService cinemaService;

    public CinemaController(CinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createCinema(
            @Valid @RequestBody CreateCinemaRequest createCinemaRequest) { //@Valid for Validation and ResponseEntity<?> for a more specific response (? = variable response type)
        //error handling
        try {
            Cinema cinema = createCinemaRequest.getCinema();
            LocationAddress locationAddress = createCinemaRequest.getLocationAddress();

            cinema.setName(cinema.getName());
            cinema.setLocationAddress(locationAddress);

            cinema = cinemaService.createCinema(cinema);

            return new ResponseEntity<>(cinema, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create cinema.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    //Get Response of specified cinema by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getCinemaById(@PathVariable Long id) {
        Cinema cinema = cinemaService.findById(id);
        if (cinema != null) {
            return new ResponseEntity<>(cinema, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Cinema not found.", HttpStatus.NOT_FOUND);
        }
    }
}
