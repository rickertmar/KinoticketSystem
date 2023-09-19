package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Discount;
import com.dhbw.kinoticket.entity.Reservation;
import com.dhbw.kinoticket.entity.Seat;
import com.dhbw.kinoticket.entity.Ticket;
import com.dhbw.kinoticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    // Get all tickets
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // Get tickets by id
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + id));
    }

    // Create ticket
    public Ticket createTicket(Discount discount, Reservation reservation, Seat seat) {
        Ticket ticket = Ticket.builder()
                .seat(seat)
                .discount(discount)
                .isValid(true)
                .reservation(reservation)
                .build();
        return ticketRepository.save(ticket);
    }

    // Book ticket

    // Delete ticket

}
