package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.Reservation;
import com.dhbw.kinoticket.response.WorkerReservationResponse;
import com.dhbw.kinoticket.service.ReservationService;
import com.dhbw.kinoticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    private final TicketService ticketService;

    @PreAuthorize("hasAuthority('worker:read')")
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<WorkerReservationResponse> getWorkerReservationById(@PathVariable Long id) {
        try {
            WorkerReservationResponse response = reservationService.getWorkerReservationById(id);
            return new ResponseEntity<>(response, HttpStatus.FOUND);
        } catch (Exception e) {
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    // ----------------------------------------------------------------
    // TicketController
    // ----------------------------------------------------------------

    // Get all tickets
    @GetMapping(value = "/tickets")
    public ResponseEntity<?> getAllTickets() {
        try {
            return new ResponseEntity<>(ticketService.getAllTickets(), HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get ticket by id
    @GetMapping(value = "/tickets/{ticketId}")
    public ResponseEntity<?> getTicketById(@PathVariable Long ticketId) {
        try {
            return new ResponseEntity<>(ticketService.getTicketById(ticketId), HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
