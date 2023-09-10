package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.repository.CinemaRepository;
import com.dhbw.kinoticket.repository.LocationAddressRepository;
import com.dhbw.kinoticket.request.CreateCinemaRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLOutput;
import java.util.List;

@Component
@Service
@RequiredArgsConstructor
public class CinemaService {

    @Autowired
    private final CinemaRepository cinemaRepository;
    @Autowired
    private final LocationAddressRepository locationAddressRepository;

    //Get all cinemas
    public List<Cinema> getAllCinemas() {
        return cinemaRepository.findAll();
    }

    //Find existing Cinema by id
    public Cinema getCinemaById(Long id) {
        List<Cinema> cinemas = cinemaRepository.findAll();
        Cinema cinema = null;
        for (Cinema cinemaRecord:cinemas) {
            if (cinemaRecord.getId() == id) {
                cinema = cinemaRecord;
            }
        }
        return cinema;
    }

    //Create new Cinema
    public Cinema createCinema(@Valid CreateCinemaRequest createCinemaRequest) {
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
        cinemaRepository.save(cinema);
        return cinema;
    }

    //Update existing Cinema
    public Cinema updateCinema(Long id,
                               @Valid CreateCinemaRequest createCinemaRequest) {
        Cinema existingCinema = getCinemaById(id);
        if (existingCinema == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CINEMA_NOT_FOUND");
        }
        existingCinema.setName(createCinemaRequest.getName());

        existingCinema.getLocationAddress().setStreet(createCinemaRequest.getStreet());
        existingCinema.getLocationAddress().setCity(createCinemaRequest.getCity());
        existingCinema.getLocationAddress().setCountry(createCinemaRequest.getCountry());
        existingCinema.getLocationAddress().setPostcode(createCinemaRequest.getPostcode());

        cinemaRepository.save(existingCinema);
        return existingCinema;
    }

    //Delete Cinema by id
    public void deleteCinema(Long id) {
        try {
            Cinema cinema = cinemaRepository.findById(id).get();
            cinemaRepository.delete(cinema);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting");
        }
    }


    // ----------------------------------------------------------------
    //LocationAddress of Cinema
    // ----------------------------------------------------------------
    //Update location address of specific cinema
    @Transactional
    public Cinema updateLocationAddress(Long id,
                                        @Valid LocationAddress locationAddress) {
        Cinema cinema = getCinemaById(id);
        Long oldLocationId = cinema.getLocationAddress().getId();
        locationAddressRepository.deleteById(oldLocationId);
        cinema.setLocationAddress(locationAddress);

        return cinemaRepository.save(cinema);
    }
}
