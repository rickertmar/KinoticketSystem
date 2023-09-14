package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.service.ShowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/showings")
public class ShowingController {

    public final ShowingService showingService;


    // Get all movies
    @GetMapping(value = "")
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
}
