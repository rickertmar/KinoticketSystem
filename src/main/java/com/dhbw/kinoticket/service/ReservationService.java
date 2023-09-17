package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.entity.Reservation;
import com.dhbw.kinoticket.repository.ReservationRepository;
import com.dhbw.kinoticket.response.WorkerReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
@RequiredArgsConstructor
public class ReservationService {

    @Autowired
    private final ReservationRepository reservationRepository;

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
