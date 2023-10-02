package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.User;
import com.dhbw.kinoticket.request.CreateReservationRequest;
import com.dhbw.kinoticket.request.EmailDetails;
import com.dhbw.kinoticket.request.UpdateSeatStatusRequest;
import com.dhbw.kinoticket.response.ReservationResponse;
import com.dhbw.kinoticket.response.WorkerReservationResponse;
import com.dhbw.kinoticket.service.*;
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
    private final ShowingService showingService;
    private final TicketService ticketService;
    private final EmailSenderService emailService;

    @PreAuthorize("hasAuthority('worker:read')")
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<WorkerReservationResponse> getWorkerReservationById(@PathVariable Long id) {
        try {
            WorkerReservationResponse response = reservationService.getWorkerReservationById(id);
            return new ResponseEntity<>(response, HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Booking: Create reservation and tickets
    @PostMapping(value = "")
    public ResponseEntity<?> createReservation(HttpServletRequest httpServletRequest,
                                               @RequestBody CreateReservationRequest request) {
        try {
            Principal principal = httpServletRequest.getUserPrincipal();
            User user = userService.getUserByEmail(principal.getName());
            ReservationResponse response = reservationService.createReservation(request, user);

            // TODO uncomment to activate email confirmation sending
            /*
            emailService.sendHtmlMail(
                    new EmailDetails(
                            user.getEmail(),
                            emailService.generateReservationEmailBodyHTML(response),
                            "Reservation Confirmation for " + response.getMovie().getTitle(),
                            null
                    )
            );
            */

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create reservation.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get Reservations of User
    @GetMapping(value = "/getUserReservations")
    public ResponseEntity<?> getReservationsByUser(HttpServletRequest httpServletRequest) {
        try {
            Principal principal = httpServletRequest.getUserPrincipal();
            User user = userService.getUserByEmail(principal.getName());
            return new ResponseEntity<>(reservationService.getReservationsByUser(user.getId()), HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to get reservations of user.", HttpStatus.INTERNAL_SERVER_ERROR);
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


    // ----------------------------------------------------------------
    // Manage status of showing seats for the reservation process
    // ----------------------------------------------------------------

    // Block seats of showing (in reservation process)
    @PutMapping(value = "/blockSeats")
    public ResponseEntity<?> blockSeats(@RequestBody UpdateSeatStatusRequest request) {
        try {
            return new ResponseEntity<>(showingService.blockSeatsOfShowing(request), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Unblock seats of showing (in reservation process)
    @PutMapping(value = "/unblockSeats")
    public ResponseEntity<?> unblockSeats(@RequestBody UpdateSeatStatusRequest request) {
        try {
            return new ResponseEntity<>(showingService.unblockSeatsOfShowing(request), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}


/*
ReservationRequest:
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


Block/Unblock Request:
{
    "showingId": 1,
    "seatIds": [1, 2, 3, 4, 5]
}

*/