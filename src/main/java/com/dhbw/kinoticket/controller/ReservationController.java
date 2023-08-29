package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.response.WorkerReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/reservation")
public class ReservationController {
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<WorkerReservationResponse> id(@PathVariable long id) {
        return ResponseEntity.ok(new WorkerReservationResponse());
    }

}
