package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.*;
import com.dhbw.kinoticket.repository.ReservationRepository;
import com.dhbw.kinoticket.repository.ShowingRepository;
import com.dhbw.kinoticket.request.CreateReservationRequest;
import com.dhbw.kinoticket.request.UpdateSeatStatusRequest;
import com.dhbw.kinoticket.response.MovieResponse;
import com.dhbw.kinoticket.response.ReservationResponse;
import com.dhbw.kinoticket.response.WorkerReservationResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ShowingRepository showingRepository;
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
        Set<Seat> seats = showing.getSeats();
        List<Long> selectedSeatIdList = request.getSelectedSeatIdList();
        int studentDiscounts = request.getStudentDiscounts();
        int childDiscounts = request.getChildDiscounts();
        int noDiscounts = request.getNoDiscounts();

        for (Long seatId : selectedSeatIdList) {

            // Discount handling per seat/ticket
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

                    // Check if the seat is still/already blocked
                    if (seat.isPermanentBlocked() || seat.isBlockingExpired()) {
                        throw new IllegalArgumentException("Seat is no longer available for booking: " + seatId);
                    }

                    // Create a ticket for each seat
                    Ticket ticket = ticketService.createTicket(discount, reservation, seat);
                    reservation.getTickets().add(ticket);

                    // Change status of seat to blocked
                    seat.setPermanentBlocked(true);

                    break; // Break out of the inner loop once a matching seat is found
                }
            }
            if (!seatFound) {
                // Handle the case when a seat with the given seatId is not found
                throw new IllegalArgumentException("Seat not found with ID: " + seatId);
            }
        }

        // Save/Update showing to update the status of booked seats to permanent blocked
        showingRepository.save(showing);

        // Calculate and set the total price for the reservation
        List<Ticket> ticketList = reservation.getTickets();
        double total = calculateTotalPrice(showing, ticketList);
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

    double calculateTotalPrice(Showing showing, List<Ticket> tickets) {
        double total = 0.0;
        for (Ticket ticket : tickets) {
            if (ticket.getDiscount().equals(Discount.STUDENT)) {
                total += (showing.getSeatPrice() - 2.0);
            } else if (ticket.getDiscount().equals(Discount.CHILD)) {
                total += (showing.getSeatPrice() - 3.0);
            } else {
                // Regular price
                total += showing.getSeatPrice();
            }
        }
        return total;
    }

    MovieResponse convertToMovieDTO(Movie movie) {
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

/*
CreateReservationRequest:
{
  "selectedSeatIdList": [1, 2],     // IDs of selected seats in a list
  "studentDiscounts": 1,            // Number of student discounts
  "childDiscounts": 1,              // Number of child discounts
  "noDiscounts": 0,                 // Number of no discounts/regulars
  "showingId": 102                  // ID of the showing
}
*/

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
