package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.dao.CreateCinemaRequest;
import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.service.CinemaService;
import com.dhbw.kinoticket.service.LocationAddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/cinemas")
public class CinemaController {
    private final CinemaService cinemaService;
    private final LocationAddressService locationAddressService;

    public CinemaController(CinemaService cinemaService, LocationAddressService locationAddressService) {
        this.cinemaService = cinemaService;
        this.locationAddressService = locationAddressService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createCinema(
            @Valid @RequestBody CreateCinemaRequest createCinemaRequest) { //@Valid for Validation and ResponseEntity<?> for a more specific response (? = variable response type)
        try { //error handling
            Cinema cinema = createCinemaRequest.getCinema();
            LocationAddress locationAddress = createCinemaRequest.getLocationAddress();

            locationAddress = locationAddressService.createLocationAddress(locationAddress);

            cinema.setLocationAddress(locationAddress);

            Cinema createdCinema = cinemaService.createCinema(cinema);

            return new ResponseEntity<>(createdCinema, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create cinema.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    //Get Response of specified cinema by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getCinemaById(@PathVariable Long id) {
        Cinema cinema = cinemaService.findById(id);
        if (cinema != null) {
            return ResponseEntity.ok(cinema);
        } else {
            return new ResponseEntity<>("Cinema not found.", HttpStatus.NOT_FOUND);
        }
    }
}

/*
Create Cinema json format
{
  "name": "TestCinema",
  "locationAddress": {
    "street": "Teststraße 1",
    "city": "Teststadt",
    "country": "Testland",
    "postcode": "12345"
  }
}
 */