package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.User;
import com.dhbw.kinoticket.request.CreateReservationRequest;
import com.dhbw.kinoticket.response.ReservationResponse;
import com.dhbw.kinoticket.response.WorkerReservationResponse;
import com.dhbw.kinoticket.service.ReservationService;
import com.dhbw.kinoticket.service.TicketService;
import com.dhbw.kinoticket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/reservation")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;
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

    // Booking: Create reservation and tickets
    @PostMapping(value = "")
    public ResponseEntity<?> createReservation(HttpServletRequest httpServletRequest,
                                               @RequestBody CreateReservationRequest request) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            User user = userService.getUserByEmail(principal.getName());
            ReservationResponse response = reservationService.createReservation(request, user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create reservation.", HttpStatus.INTERNAL_SERVER_ERROR);
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


/*

CreateReservationRequest:
{
  "selectedSeatIdList": [1, 2],
  "discountList": ["REGULAR", "STUDENT"],
  "isPaid": true,
  "showingId": 1
}

*/