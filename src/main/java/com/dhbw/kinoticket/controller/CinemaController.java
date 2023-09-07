package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.entity.CinemaHall;
import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.entity.Seat;
import com.dhbw.kinoticket.repository.CinemaHallRepository;
import com.dhbw.kinoticket.repository.CinemaRepository;
import com.dhbw.kinoticket.repository.LocationAddressRepository;
import com.dhbw.kinoticket.request.CreateCinemaRequest;
import com.dhbw.kinoticket.request.CreateSeatRequest;
import com.dhbw.kinoticket.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cinemas")
public class CinemaController {
    private final CinemaService cinemaService;
    private final CinemaRepository cinemaRepository;
    private final LocationAddressRepository locationAddressRepository;
    private final CinemaHallRepository cinemaHallRepository;

    //Create Cinema
    @PreAuthorize("hasAuthority('admin:create')")
    @PostMapping(value = "")
    public ResponseEntity<?> createCinema(@RequestBody CreateCinemaRequest createCinemaRequest) {
        var locationAddress = LocationAddress
                .builder()
                .street(createCinemaRequest.getStreet())
                .city(createCinemaRequest.getCity())
                .country(createCinemaRequest.getCountry())
                .postcode(createCinemaRequest.getPostcode())
                .build();
        var createdLocation = locationAddressRepository.save(locationAddress);
        Cinema cinema = Cinema
                .builder()
                .name(createCinemaRequest.getName())
                .build();
        cinema.setLocationAddress(createdLocation);
        var createdCinema = cinemaRepository.save(cinema);
        return new ResponseEntity<>(createdCinema, HttpStatus.CREATED);
    }

    //Get Response of specified cinema by id
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getCinemaById(@PathVariable Long id) {
        Cinema cinema = cinemaService.findById(id);
        if (cinema != null) {
            return ResponseEntity.ok(cinema);
        } else {
            return new ResponseEntity<>("Cinema not found.", HttpStatus.NOT_FOUND);
        }
    }

    //Delete specific cinema
    @PreAuthorize("hasAuthority('admin:delete')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCinema(@PathVariable Long id) {
        cinemaService.deleteCinema(id);
        return new ResponseEntity<>("Cinema deleted.", HttpStatus.OK);
    }


    // ----------------------------------------------------------------
    //LocationAddress of Cinema
    // ----------------------------------------------------------------
    //Update location address of specific cinema
    @PreAuthorize("hasAuthority('admin:update')")
    @PutMapping(value = "/{id}/location")
    public ResponseEntity<?> updateLocationAddress(@PathVariable Long id,
                                                   @RequestBody LocationAddress locationAddress) {
        try {
            Optional<Cinema> optionalCinema = cinemaRepository.findById(id);
            if (optionalCinema.isPresent()) {
                Cinema cinema = optionalCinema.get();
                cinema.setLocationAddress(locationAddress);
                return new ResponseEntity<>(cinemaRepository.save(cinema).getLocationAddress(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Cinema not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update LocationAddress", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // ----------------------------------------------------------------
    //CinemaHall "Controller"
    // ----------------------------------------------------------------
    //Create and add CinemaHall
    @PreAuthorize("hasAuthority('admin:create')")
    @PostMapping(value = "/{id}/cinemahalls")
    public ResponseEntity<?> addCinemaHall(@PathVariable Long id) {
        try {
            //Operate in specified cinema
            Optional<Cinema> optionalCinema = cinemaRepository.findById(id);
            if (optionalCinema.isPresent()) {
                Cinema cinema = optionalCinema.get();
                //Build the new CinemaHall object
                var cinemaHall = CinemaHall.builder()
                        .build();
                cinema.getCinemaHallList().add(cinemaHall);
                cinemaHall.setCinema(cinema);
                //cinemaRepository.save(cinema); unnecessary -> caused double creation of cinemahall objects
                return new ResponseEntity<>(cinemaHallRepository.save(cinemaHall), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Cinema not found.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add CinemaHall.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Add seats to CinemaHall
    @PreAuthorize("hasAuthority('admin:create')")
    @PostMapping(value = "/{cinemaId}/cinemahalls/{cinemaHallId}/seats")
    public ResponseEntity<?> addSeatsToCinemaHall(
            @PathVariable Long cinemaId,
            @PathVariable Long cinemaHallId,
            @RequestBody List<CreateSeatRequest> seatDTOList) {
        try {
            Optional<CinemaHall> optionalCinemaHall = cinemaHallRepository.findById(cinemaHallId);
            if (optionalCinemaHall.isPresent()) {
                CinemaHall cinemaHall = optionalCinemaHall.get();
                List<Seat> seats = convertSeatDTOsToSeats(seatDTOList, cinemaHall);
                cinemaHall.getSeats().addAll(seats);
                cinemaHallRepository.save(cinemaHall);
                return new ResponseEntity<>(cinemaHall, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("CinemaHall not found.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add seats to CinemaHall.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Get specific CinemaHall of Cinema
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping(value = "/{cinemaId}/cinemahalls/{cinemaHallId}")
    public ResponseEntity<?> getCinemaHall(@PathVariable Long cinemaId,
                                           @PathVariable Long cinemaHallId) {
        Optional<CinemaHall> optionalCinemaHall = cinemaHallRepository.findById(cinemaHallId);
        if (optionalCinemaHall.isPresent()) {
            CinemaHall cinemaHall = optionalCinemaHall.get();
            return ResponseEntity.ok(cinemaHall);
        } else {
            return new ResponseEntity<>("CinemaHall not found.", HttpStatus.NOT_FOUND);
        }
    }

    //Update SeatsList of a CinemaHall object
    @PreAuthorize("hasAuthority('admin:update')")
    @PutMapping(value = "/{cinemaId}/cinemahalls/{cinemaHallId}/seats")
    public ResponseEntity<?> updateCinemaHallSeats(
            @PathVariable Long cinemaId,
            @PathVariable Long cinemaHallId,
            @RequestBody List<CreateSeatRequest> seatDTOList) {
        try {
            Optional<CinemaHall> optionalCinemaHall = cinemaHallRepository.findById(cinemaHallId);
            //Optional<Cinema> optionalCinema = cinemaRepository.findById(cinemaId);
            //Cinema shouldn't be effected by the update of the CinemaHall
            if (optionalCinemaHall.isPresent()) {  //&& optionalCinema.isPresent()
                CinemaHall updatedCinemaHall = optionalCinemaHall.get();
                List<Seat> seats = convertSeatDTOsToSeats(seatDTOList, updatedCinemaHall);
                updatedCinemaHall.setSeats(seats);
                //Cinema cinema = optionalCinema.get();
                return new ResponseEntity<>(cinemaHallRepository.save(updatedCinemaHall), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("CinemaHall of Cinema not found.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update CinemaHall", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Delete CinemaHall
    @PreAuthorize("hasAuthority('admin:delete')")
    @DeleteMapping("/{cinemaId}/cinemahalls/{cinemaHallId}")
    public ResponseEntity<?> deleteCinemaHall(@PathVariable Long cinemaHallId,
                                              @PathVariable String cinemaId) {
        try {
            Optional<CinemaHall> optionalCinemaHall = cinemaHallRepository.findById(cinemaHallId);
            if (optionalCinemaHall.isPresent()) {
                CinemaHall cinemaHall = optionalCinemaHall.get();
                Cinema cinema = cinemaHall.getCinema();
                cinema.getCinemaHallList().remove(cinemaHall);
                cinemaRepository.save(cinema);
                cinemaHallRepository.delete(cinemaHall);
                return new ResponseEntity<>("CinemaHall deleted successfully.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("CinemaHall not found.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete CinemaHall.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Method to convert seat dto object to seats list
    private List<Seat> convertSeatDTOsToSeats(List<CreateSeatRequest> seatDTOList,
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

/*
Create Cinema json format
{
    "name": "Lichtspielhaus",
    "street": "Lennestrasse 1",
    "city": "Altenhundem",
    "country": "Germany",
    "postcode": "58368"
}

Add Seats to CinemaHall
[
    {
        "seatRow": 1,
        "number": 101,
        "xloc": 10,
        "yloc": 20,
        "blocked": false
    },
    {
        "seatRow": 1,
        "number": 102,
        "xloc": 20,
        "yloc": 20,
        "blocked": true
    },
    {
        "seatRow": 1,
        "number": 103,
        "xloc": 30,
        "yloc": 20,
        "blocked": false
    }
]

 */