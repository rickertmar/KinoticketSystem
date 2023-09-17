package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.*;
import com.dhbw.kinoticket.repository.ReservationRepository;
import com.dhbw.kinoticket.request.CreateReservationRequest;
import com.dhbw.kinoticket.response.ReservationResponse;
import com.dhbw.kinoticket.response.WorkerReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Component
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ShowingService showingService;
    private final TicketService ticketService;

    public WorkerReservationResponse getWorkerReservationById(Long id) {
        List<Reservation> reservations = reservationRepository.findAll();
        Reservation reservation = null;
        for (Reservation reservationsRecord:reservations) {
            if (reservationsRecord.getId() == id) {
                reservation = reservationsRecord;
            }
        }
        return convertToWorkerResponse(reservation);
    }

    // Booking: Create reservation and tickets
    public ReservationResponse createReservation(CreateReservationRequest request, User user) {
        // Initialize reservation with user
        Reservation reservation = Reservation.builder()
                .user(user)
                .build();

        // Fetch showing from repository
        Showing showing = showingService.getShowingById(request.getShowingId());
        List<Seat> seats = showing.getCinemaHall().getSeats();

        // Iterate through requested seat IDs
        for (Long seatId : request.getSelectedSeatIdList()) {
            for (Seat seat : seats) {
                if (seat.getId().equals(seatId)) {
                    // Create and add ticket to reservation for each discount
                    for (Discount discount : request.getDiscountList()) {
                        Ticket ticket = ticketService.createTicket(discount, reservation, seat);
                        reservation.getTickets().add(ticket);
                    }
                }
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
        reservation.setPaid(request.isPaid());
        reservationRepository.save(reservation);

        // Return ReservationResponse entity
        return ReservationResponse.builder()
                .movie(showing.getMovie())
                .time(showing.getTime())
                .tickets(ticketList)
                .total(total)
                .build();
    }

    public WorkerReservationResponse convertToWorkerResponse(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        var response = WorkerReservationResponse.builder()
                .id(reservation.getId())
                .total(reservation.getTotal())
                .isPaid(reservation.isPaid())
                .userName(null) // TODO Logic missing
                .movieStart(null) // TODO Logic missing
                .movieName(null) // TODO Logic missing
                .tickets(reservation.getTickets())
                .build();
        return response;
    }
}
