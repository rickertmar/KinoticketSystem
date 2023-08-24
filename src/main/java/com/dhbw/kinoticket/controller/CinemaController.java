package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.Cinema;
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
    public ResponseEntity<?> createCinema(@Valid @RequestBody Cinema cinemaRequest) { //@Valid for Validation and ResponseEntity<?> for a more specific response (? = variable response type)
        //error handling
        try {
            Cinema cinema = new Cinema();
            cinema.setName(cinemaRequest.getName());
            cinema.setLocationAddress(cinemaRequest.getLocationAddress());

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
