package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.entity.Movie;
import com.dhbw.kinoticket.repository.CinemaRepository;
import com.dhbw.kinoticket.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final CinemaRepository cinemaRepository;
    private final CinemaService cinemaService;

    // Get all movies
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // Get movie by id
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with ID: " + id));
    }

    // Create movie entity and add it to the cinema
    public Movie addMovieToCinema(Long cinemaId, Movie movie) {
        Cinema cinema = cinemaService.getCinemaById(cinemaId);
        if (cinema == null) {
            throw new IllegalArgumentException("Cinema not found with ID: " + cinemaId);
        }
        if (movie == null) {
            throw new IllegalArgumentException("Invalid or missing movie data");
        }
        movie.setCinema(cinema);
        cinema.getMovieList().add(movie);
        cinemaRepository.save(cinema);
        return movie;
    }

    // Remove movie from the cinema
    public void removeMovie(Long movieId) {
        Movie movie = getMovieById(movieId);

        Cinema cinema = movie.getCinema();
        if (cinema != null) {
            cinema.getMovieList().remove(movie);
            cinemaRepository.save(cinema);
        }

        movieRepository.delete(movie);
    }
}
