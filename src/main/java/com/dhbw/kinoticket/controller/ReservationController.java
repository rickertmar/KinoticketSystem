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

{
  "selectedSeatIdList": [1, 2],
  "studentDiscounts": 1,
  "childDiscounts": 1,
  "noDiscounts": 0,
  "showingId": 102
}

ReservationResponse:
{
    "movie": {
        "id": 2,
        "title": "Movie 1",
        "fsk": "FSK12",
        "description": "Description 1",
        "releaseYear": 2021,
        "genres": "Genre 1",
        "director": "Director 1",
        "runningWeek": 1,
        "runtime": "120 minutes",
        "releaseCountry": "Country 1",
        "imageSrc": "image1.jpg",
        "actors": "Actor 1"
    },
    "time": "2022-01-01T10:00:00",
    "tickets": [
        {
            "id": 1,
            "seat": {
                "id": 1,
                "seatRow": "A",
                "number": 1,
                "blocked": true,
                "xloc": 10,
                "yloc": 20
            },
            "discount": "CHILD",
            "valid": true
        },
        {
            "id": 2,
            "seat": {
                "id": 2,
                "seatRow": "B",
                "number": 2,
                "blocked": true,
                "xloc": 20,
                "yloc": 20
            },
            "discount": "STUDENT",
            "valid": true
        }
    ],
    "total": 5.0
}

*/