package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Movie;
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

    @Autowired
    private final MovieRepository movieRepository;

    // Get all movies
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // Get movie by id
    public Movie getMovieById(Long id) {
        List<Movie> movies = movieRepository.findAll();
        Movie movie = null;
        for (Movie movieRecord:movies) {
            if (movieRecord.getId() == id) {
                movie = movieRecord;
            }
        }
        return movie;
    }

    // Create movie entity and add it to the cinema
    public Movie addMovieToCinema(Movie movie) {
        return null;
    }

    // Remove movie from the cinema
    public void removeMovie(Movie movie) {

    }
}
