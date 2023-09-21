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
    public ReservationResponse createReservation(CreateReservationRequest request, User user) { // TODO prevent from creating single tickets twice
        // Initialize reservation with user
        Reservation reservation = Reservation.builder()
                .user(user)
                .tickets(new ArrayList<>())
                .build();

        // Fetch showing from repository
        Showing showing = showingService.getShowingById(request.getShowingId());
        reservation.setShowing(showing);

        // Iterate through requested seat IDs
        List<Seat> seats = showing.getCinemaHall().getSeats(); // TODO change to (free)SeatsList of showing
        List<Long> selectedSeatIdList = request.getSelectedSeatIdList();
        List<Discount> discountList = request.getDiscountList();
        for (int i = 0; i < selectedSeatIdList.size(); i++) {
            Long seatId = selectedSeatIdList.get(i);
            boolean seatFound = false;
            for (Seat seat : seats) {
                if (seat.getId().equals(seatId)) {
                    seatFound = true;
                    Discount discount = discountList.get(i);
                    // Create a ticket for each seat with the corresponding discount
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
                .userName(null) // TODO Logic missing
                .movieStart(null) // TODO Logic missing
                .movieName(null) // TODO Logic missing
                .tickets(reservation.getTickets())
                .build();
    }
}
