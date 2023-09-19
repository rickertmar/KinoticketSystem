package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.entity.CinemaHall;
import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.entity.Movie;
import com.dhbw.kinoticket.request.CreateCinemaRequest;
import com.dhbw.kinoticket.request.CreateSeatRequest;
import com.dhbw.kinoticket.service.CinemaHallService;
import com.dhbw.kinoticket.service.CinemaService;
import com.dhbw.kinoticket.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cinemas")
public class CinemaController {
    private final CinemaService cinemaService;
    private final CinemaHallService cinemaHallService;
    private final MovieService movieService;


    //Get all cinemas
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping
    public ResponseEntity<List<Cinema>> getAllCinemas() {
        try {
            List<Cinema> cinemas = cinemaService.getAllCinemas();
            return new ResponseEntity<>(cinemas, HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Get Response of specified cinema by id
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getCinemaById(@PathVariable Long id) {
        Cinema cinema = cinemaService.getCinemaById(id);

        if (cinema != null) {
            return new ResponseEntity<>(cinema, HttpStatus.FOUND);
        } else {
            return new ResponseEntity<>("Cinema not found.", HttpStatus.NOT_FOUND);
        }
    }

    //Create Cinema
    @PreAuthorize("hasAuthority('admin:create')")
    @PostMapping(value = "")
    public ResponseEntity<?> createCinema(@RequestBody CreateCinemaRequest createCinemaRequest) {
        try {
            Cinema createCinema = cinemaService.createCinema(createCinemaRequest);
            return new ResponseEntity<>(createCinema, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Update Cinema
    @PreAuthorize("hasAuthority('admin:update')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateCinema(@PathVariable Long id,
                                          @RequestBody CreateCinemaRequest createCinemaRequest) {
        try {
            return new ResponseEntity<>(cinemaService.updateCinema(id, createCinemaRequest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    //Delete specific cinema
    @PreAuthorize("hasAuthority('admin:delete')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCinema(@PathVariable Long id) {
        try {
            cinemaService.deleteCinema(id);
            return new ResponseEntity<>("Cinema deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete Cinema.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

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
            return new ResponseEntity<>(cinemaService.updateLocationAddress(id, locationAddress), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update LocationAddress", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // ----------------------------------------------------------------
    //CinemaHall "Controller"
    // ----------------------------------------------------------------

    //Get specific CinemaHall of Cinema
    //@PreAuthorize("hasAuthority('admin:read')") // needs to be reached for CinemaHall plan in frontend
    @GetMapping(value = "/{cinemaId}/cinemahalls/{cinemaHallId}")
    public ResponseEntity<?> getCinemaHall(@PathVariable Long cinemaId,
                                           @PathVariable Long cinemaHallId) {
        try {
            CinemaHall cinemaHall = cinemaHallService.getCinemaHallById(cinemaHallId);
            return new ResponseEntity<>(cinemaHall, HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Create and add CinemaHall
    @PreAuthorize("hasAuthority('admin:create')")
    @PostMapping(value = "/{id}/cinemahalls")
    public ResponseEntity<?> addCinemaHall(@PathVariable Long id,
                                           @RequestBody String name) {
        try {
            return new ResponseEntity<>(cinemaHallService.createCinemaHallAndAddToCinema(id, name), HttpStatus.CREATED);
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
            return new ResponseEntity<>(cinemaHallService.addSeatsToCinemaHall(cinemaHallId, seatDTOList), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add seats to CinemaHall.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Update CinemaHall object
    @PreAuthorize("hasAuthority('admin:update')")
    @PutMapping(value = "/{cinemaId}/cinemahalls/{cinemaHallId}/seats")
    public ResponseEntity<?> updateCinemaHall(
            @PathVariable Long cinemaId,
            @PathVariable Long cinemaHallId,
            @RequestParam(name = "New_Name") String name,
            @RequestBody List<CreateSeatRequest> seatDTOList) {
        try {
            return new ResponseEntity<>(cinemaHallService.updateSeatsOfCinemaHall(cinemaHallId, name, seatDTOList), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update CinemaHall.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Delete CinemaHall
    @PreAuthorize("hasAuthority('admin:delete')")
    @DeleteMapping(value = "/{cinemaId}/cinemahalls/{cinemaHallId}")
    public ResponseEntity<?> deleteCinemaHall(@PathVariable Long cinemaHallId,
                                              @PathVariable String cinemaId) {
        try {
            cinemaHallService.deleteCinemaHall(cinemaHallId);
            return new ResponseEntity<>("CinemaHall deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete CinemaHall.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // ----------------------------------------------------------------
    //Movie "Controller"
    // ----------------------------------------------------------------

    // Get all movies
    @GetMapping(value = "/{cinemaId}/movies")
    public ResponseEntity<?> getAllMovies() {
        try {
            return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get movie by id
    @GetMapping(value = "/{cinemaId}/movies/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(movieService.getMovieById(id), HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Create movie entity and add it to the cinema
    @PostMapping(value = "/{cinemaId}/movies")
    public ResponseEntity<?> addMovieToCinema(@PathVariable Long cinemaId,
                                              @RequestBody Movie movie) {
        try {
            return new ResponseEntity<>(movieService.addMovieToCinema(cinemaId, movie), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    // Delete movie by id and remove from associated cinema
    @DeleteMapping(value = "/{cinemaId}/movies/{movieId}")
    public ResponseEntity<?> removeMovieById(@PathVariable Long movieId) {
        try {
            movieService.removeMovie(movieId);
            return new ResponseEntity<>("Movie deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
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

Create CinemaHall -> Text format
TestCinemaHallName

Add Seats to CinemaHall
[
    {
        "seatRow": "A",
        "number": 1,
        "xloc": 10,
        "yloc": 20,
        "blocked": false
    },
    {
        "seatRow": "A",
        "number": 2,
        "xloc": 20,
        "yloc": 20,
        "blocked": true
    },
    {
        "seatRow": "A",
        "number": 3,
        "xloc": 30,
        "yloc": 20,
        "blocked": false
    }
]

Add movie
{
    "title": "Movie 1",
    "fsk": "FSK12",
    "description": "Description 1",
    "releaseYear": 2021,
    "genres": "Genre 1",
    "director": "Director 1",
    "runningWeek": 1,
    "runtime": "120 minutes",
    "releaseCountry": "Country 1",
    "imageSrc": "image1.jpg",
    "actors": "Actor 1"
}

 */