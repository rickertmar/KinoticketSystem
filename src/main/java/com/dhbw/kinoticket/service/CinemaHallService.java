package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.entity.CinemaHall;
import com.dhbw.kinoticket.entity.Seat;
import com.dhbw.kinoticket.repository.CinemaHallRepository;
import com.dhbw.kinoticket.repository.CinemaRepository;
import com.dhbw.kinoticket.repository.SeatRepository;
import com.dhbw.kinoticket.request.CreateSeatRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
@Service
@RequiredArgsConstructor
public class CinemaHallService {

    private final SeatRepository seatRepository;
    private final CinemaHallRepository cinemaHallRepository;
    private final CinemaRepository cinemaRepository;
    private final CinemaService cinemaService;


    //Get CinemaHall by id
    public CinemaHall getCinemaHallById(Long id) {
        return cinemaHallRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("CinemaHall not found with ID: " + id));
    }

    //Create CinemaHall and add to Cinema
    public CinemaHall createCinemaHallAndAddToCinema(Long id,
                                                     String name) {
        Cinema cinema = cinemaService.getCinemaById(id);
        //Build the new CinemaHall object
        var cinemaHall = CinemaHall.builder()
                .name(name)
                .build();
        cinema.getCinemaHallList().add(cinemaHall);
        cinemaHall.setCinema(cinema);
        cinemaHallRepository.save(cinemaHall);
        cinemaRepository.save(cinema);
        return cinemaHall;
    }

    //Add seats to CinemaHall
    public CinemaHall addSeatsToCinemaHall(Long id,
                                           List<CreateSeatRequest> createSeatRequests) {
        try{
            CinemaHall cinemaHall = getCinemaHallById(id);
            List<Seat> seats = convertSeatDTOsToSeats(createSeatRequests, cinemaHall);
            cinemaHall.getSeats().addAll(seats);
            cinemaHallRepository.save(cinemaHall);
            return cinemaHall;
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cinema hall not found");
        }
    }

    //Update CinemaHall object
    @Transactional
    public CinemaHall updateSeatsOfCinemaHall(Long id,
                                              String name,
                                              List<CreateSeatRequest> createSeatRequests) {
        CinemaHall cinemaHall = getCinemaHallById(id);
        List<Seat> oldSeatList = cinemaHall.getSeats();
        cinemaHall.setSeats(null);
        List<Seat> newSeats = convertSeatDTOsToSeats(createSeatRequests, cinemaHall);
        cinemaHall.setName(name);
        cinemaHall.setSeats(newSeats);
        cinemaHallRepository.save(cinemaHall);
        seatRepository.deleteAll(oldSeatList);
        return cinemaHall;
    }

    //Delete CinemaHall
    @Transactional
    public void deleteCinemaHall(Long id) {
        try {
            CinemaHall cinemaHall = getCinemaHallById(id);
            Cinema cinema = cinemaHall.getCinema();
            cinema.getCinemaHallList().remove(cinemaHall);
            cinemaRepository.save(cinema);
            cinemaHallRepository.delete(cinemaHall);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting");
        }
    }


    //Method to convert seat dto object to seats list
    public List<Seat> convertSeatDTOsToSeats(List<CreateSeatRequest> seatDTOList,
                                              CinemaHall cinemaHall) {
        List<Seat> seats = new ArrayList<>();
        for (CreateSeatRequest seatDTO : seatDTOList) {
            var seat = Seat.builder()
                    .seatRow(seatDTO.getSeatRow())
                    .number(seatDTO.getNumber())
                    .xLoc(seatDTO.getXLoc())
                    .yLoc(seatDTO.getYLoc())
                    .isBlocked(seatDTO.isBlocked())
                    .cinemaHall(cinemaHall)
                    .build();
            seats.add(seat);
        }
        return seats;
    }
}
