package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.CinemaHall;
import com.dhbw.kinoticket.entity.Movie;
import com.dhbw.kinoticket.entity.Seat;
import com.dhbw.kinoticket.entity.Showing;
import com.dhbw.kinoticket.repository.MovieRepository;
import com.dhbw.kinoticket.repository.ShowingRepository;
import com.dhbw.kinoticket.request.CreateShowingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Service
@RequiredArgsConstructor
public class ShowingService {

    @Autowired
    private final ShowingRepository showingRepository;
    @Autowired
    private final MovieRepository movieRepository;
    @Autowired
    private final MovieService movieService;
    @Autowired
    private final CinemaHallService cinemaHallService;

    // Get all showings
    public List<Showing> getAllShowings() {
        return showingRepository.findAll();
    }

    // Get showing by id
    public Showing getShowingById(Long id) {
        return showingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Showing not found with ID: " + id));
    }

    // Create showing
    public Showing createShowing(CreateShowingRequest request) {
        Movie movie = movieService.getMovieById(request.getMovieId());
        CinemaHall cinemaHall = cinemaHallService.getCinemaHallById(request.getCinemaHallId());
        Showing showing = Showing.builder()
                .movie(movie)
                .time(request.getTime())
                .showingExtras(request.getShowingExtras())
                .seatPrice(request.getSeatPrice())
                .cinemaHall(cinemaHall)
                .build();

        Set<Seat> seats = getSeatsOfCinemaHall(request.getCinemaHallId());
        Set<Seat> copiedSeats = new HashSet<>();
        for (Seat seat : seats) {
            Seat copiedSeat = Seat.builder()
                    .seatRow(seat.getSeatRow())
                    .number(seat.getNumber())
                    .xLoc(seat.getXLoc())
                    .yLoc(seat.getYLoc())
                    .isBlocked(seat.isBlocked())
                    .cinemaHall(cinemaHall)
                    .build();
            copiedSeats.add(copiedSeat);
        }
        showing.setFreeSeats(copiedSeats);

        return showingRepository.save(showing);
    }

    // Update showing
    public Showing updateShowing(Long showingId, CreateShowingRequest createShowingRequest) {
        Showing existingShowing = getShowingById(showingId);

        existingShowing.setMovie(movieService.getMovieById(createShowingRequest.getMovieId()));
        existingShowing.setTime(createShowingRequest.getTime());
        existingShowing.setShowingExtras(createShowingRequest.getShowingExtras());
        existingShowing.setCinemaHall(cinemaHallService.getCinemaHallById(createShowingRequest.getCinemaHallId()));

        return showingRepository.save(existingShowing);
    }

    // Delete showing by id
    public void deleteShowing(Long id) {
        Showing showing = getShowingById(id);
        showingRepository.delete(showing);
    }


    // Get seats of CinemaHall and convert list to set
    public Set<Seat> getSeatsOfCinemaHall(Long cinemaHallId) {
        CinemaHall cinemaHall = cinemaHallService.getCinemaHallById(cinemaHallId);
        List<Seat> seats = cinemaHall.getSeats();
        Set<Seat> seatSet = new HashSet<>(seats);
        return seatSet;
    }

    // Test if Movie exists
    public boolean doesMovieExist(Long movieId) {
        return movieRepository.existsById(movieId);
    }
}

/*

Create showing: CreateShowingRequest json format

{
    "time": "2022-12-31T20:00:00",
    "showingExtras": "3D",
    "movieId": 1,
    "cinemaHallId": 1
}

*/
