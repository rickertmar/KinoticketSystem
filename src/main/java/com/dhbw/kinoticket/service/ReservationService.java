package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.*;
import com.dhbw.kinoticket.repository.ReservationRepository;
import com.dhbw.kinoticket.request.CreateReservationRequest;
import com.dhbw.kinoticket.response.MovieResponse;
import com.dhbw.kinoticket.response.ReservationResponse;
import com.dhbw.kinoticket.response.WorkerReservationResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ShowingService showingService;
    private final TicketService ticketService;

    public WorkerReservationResponse getWorkerReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Showing not found with ID: " + id));
        return convertToWorkerResponse(reservation);
    }


    // Booking: Create reservation and tickets
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request, User user) {
        // Initialize reservation with user
        Reservation reservation = Reservation.builder()
                .user(user)
                .tickets(new ArrayList<>())
                .build();

        // Fetch showing from repository
        Showing showing = showingService.getShowingById(request.getShowingId());
        reservation.setShowing(showing);

        // Iterate through requested seat IDs
        Set<Seat> seatsSet = showing.getSeats();
        List<Seat> seats = new ArrayList<>(seatsSet);
        List<Long> selectedSeatIdList = request.getSelectedSeatIdList();
        int studentDiscounts = request.getStudentDiscounts();
        int childDiscounts = request.getChildDiscounts();
        int noDiscounts = request.getNoDiscounts();

        for (Long seatId : selectedSeatIdList) {

            // Disount handling per seat/ticket
            Discount discount = null;
            if (noDiscounts > 0) {
                discount = Discount.REGULAR;
                noDiscounts -= 1;
            } else if (childDiscounts > 0) {
                discount = Discount.CHILD;
                childDiscounts -= 1;
            } else if (studentDiscounts > 0) {
                discount = Discount.STUDENT;
                studentDiscounts -= 1;
            }

            boolean seatFound = false;
            for (Seat seat : seats) {
                if (seat.getId().equals(seatId)) {
                    seatFound = true;

                    // Create a ticket for each seat
                    Ticket ticket = ticketService.createTicket(discount, reservation, seat);
                    reservation.getTickets().add(ticket);
                    break; // Break out of the inner loop once a matching seat is found
                }
            }
            if (!seatFound) {
                // Handle the case when a seat with the given seatId is not found
                throw new IllegalArgumentException("Seat not found with ID: " + seatId);
            }
        }

        // Calculate and set the total price for the reservation
        double total = 0.0;
        List<Ticket> ticketList = reservation.getTickets();
        for (Ticket ticket : ticketList) {
            if (ticket.getDiscount().equals(Discount.STUDENT)) {
                total += (showing.getSeatPrice() - 2.0);
            } else if (ticket.getDiscount().equals(Discount.CHILD)) {
                total += (showing.getSeatPrice() - 3.0);
            } else {
                // Regular price
                total += showing.getSeatPrice();
            }
        }
        reservation.setTotal(total);

        // Mark reservation as paid and save it
        reservation.setPaid(true);
        reservationRepository.save(reservation);

        // Convert Movie entity to MovieDTO
        MovieResponse movieResponse = convertToMovieDTO(showing.getMovie());

        // Return ReservationResponse entity
        return ReservationResponse.builder()
                .movie(movieResponse)
                .time(showing.getTime())
                .tickets(ticketList)
                .total(total)
                .build();
    }

/*
CreateReservationRequest:
{
  "selectedSeatIdList": [1, 2],     // IDs of selected seats in a list
  "studentDiscounts": 1,            // Number of student discounts
  "childDiscounts": 1,                 // Number of child discounts
  "noDiscounts": 0,                    // Number of no discounts/regulars
  "showingId": 102                    // ID of the showing
}
*/


    private MovieResponse convertToMovieDTO(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .fsk(movie.getFsk())
                .description(movie.getDescription())
                .releaseYear(movie.getReleaseYear())
                .genres(movie.getGenres())
                .director(movie.getDirector())
                .runningWeek(movie.getRunningWeek())
                .runtime(movie.getRuntime())
                .releaseCountry(movie.getReleaseCountry())
                .imageSrc(movie.getImageSrc())
                .actors(movie.getActors())
                .build();
    }

    public WorkerReservationResponse convertToWorkerResponse(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        return WorkerReservationResponse.builder()
                .id(reservation.getId())
                .total(reservation.getTotal())
                .isPaid(reservation.isPaid())
                .userName(reservation.getUser().getEmail())
                .movieStart(reservation.getShowing().getTime())
                .movieName(reservation.getShowing().getMovie().getTitle())
                .tickets(reservation.getTickets())
                .build();
    }
}
