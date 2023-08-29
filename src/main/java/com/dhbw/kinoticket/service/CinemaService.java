package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.repository.CinemaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CinemaService {

    private final CinemaRepository cinemaRepository;

    public CinemaService(CinemaRepository cinemaRepository) {
        this.cinemaRepository = cinemaRepository;
    }

    //Create new Cinema
    public Cinema createCinema(@Valid Cinema cinema) {
        return cinemaRepository.save(cinema);
    }

    //Update existing Cinema
    public Cinema updateCinema(Cinema cinema, Long id) {
        Cinema existingCinema = findById(id);
        if (existingCinema == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CINEMA_NOT_FOUND");
        }
        existingCinema.setName(cinema.getName());
        existingCinema.setLocationAddress(cinema.getLocationAddress());
        return cinemaRepository.save(existingCinema);
    }

    //Delete Cinema by id
    public void deleteCinema(Long id) {
        cinemaRepository.deleteById(id);
    }

    //Find existing Cinema by id
    public Cinema findById(Long id) {
        return cinemaRepository.findById(id).orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND, "CINEMA_NOT_FOUND"));
    }

    //Find existing Cinema by name
    public Cinema findByName(String name) {
        return cinemaRepository.findByName(name);
    }

    //Call all Cinemas
    public Iterable<Cinema> findAll() {
        return cinemaRepository.findAll();
    }
}
