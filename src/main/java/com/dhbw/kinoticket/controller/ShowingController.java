package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.Seat;
import com.dhbw.kinoticket.request.CreateShowingRequest;
import com.dhbw.kinoticket.service.CinemaHallService;
import com.dhbw.kinoticket.service.ShowingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/showings")
public class ShowingController {

    @Autowired
    private final ShowingService showingService;
    @Autowired
    private final CinemaHallService cinemaHallService;


    // Get all movies
    @GetMapping
    public ResponseEntity<?> getAllShowings() {
        try {
            return new ResponseEntity<>(showingService.getAllShowings(), HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get movie by id
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getShowingById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(showingService.getShowingById(id), HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get seats of showing
    @GetMapping(value = "/{id}/getSeats")
    public ResponseEntity<?> getSeatsOfShowing(@PathVariable Long id) {
        try {
            Set<Seat> seats = showingService.getSeatsOfShowing(id);
            return new ResponseEntity<>(seats, HttpStatus.FOUND);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create showing
    @PreAuthorize("hasAuthority('admin:create')")
    @PostMapping
    public ResponseEntity<?> createShowing(@RequestBody CreateShowingRequest request) {
        try {
            Long movieId = request.getMovieId();
            if (!showingService.doesMovieExist(movieId)) {
                return new ResponseEntity<>("Movie does not exist.", HttpStatus.BAD_REQUEST);
            }
            Long cinemaHallId = request.getCinemaHallId();
            if (!cinemaHallService.doesCinemaHallExist(cinemaHallId)) {
                return new ResponseEntity<>("CinemaHall does not exist.", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(showingService.createShowing(request), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update showing
    @PreAuthorize("hasAuthority('admin:update')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateShowing(@PathVariable Long id,
                                           @RequestBody CreateShowingRequest createShowingRequest) {
        try {
            return new ResponseEntity<>(showingService.updateShowing(id, createShowingRequest), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete showing
    @PreAuthorize("hasAuthority('admin:delete')")
    @DeleteMapping("/{showingId}")
    public ResponseEntity<?> deleteShowing(@PathVariable Long showingId) {
        try {
            showingService.deleteShowing(showingId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

/*

{
    "time": "2022-12-31T20:00:00",
    "showingExtras": "3D",
    "movieId": 1,
    "cinemaHallId": 1,
    "seatPrice": 5.0
}

*/