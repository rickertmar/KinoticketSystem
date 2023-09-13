package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.FSK;
import com.dhbw.kinoticket.entity.Movie;
import com.dhbw.kinoticket.repository.MovieRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {MovieServiceTest.class})
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie movie1;
    private Movie movie2;
    private List<Movie> allMovies;

    @BeforeEach
    public void setUp() {
        movie1 = new Movie(
                1L,
                "Movie 1",
                FSK.FSK12,
                "Description 1",
                2021,
                "Genre 1",
                "Director 1",
                1,
                "120 minutes",
                "Country 1",
                "image1.jpg",
                "Actor 1",
                null
        );

        movie2 = new Movie(
                2L,
                "Movie 2",
                FSK.FSK16,
                "Description 2",
                2022,
                "Genre 2",
                "Director 2",
                2,
                "110 minutes",
                "Country 2",
                "image2.jpg",
                "Actor 2",
                null
        );
        allMovies = Arrays.asList(movie1, movie2);
    }


    @Test
    @Order(1)
    public void test_GetAllMovies_WhenCalled_ThenRepositoryFindAllIsCalled() {
        // Mock
        when(movieRepository.findAll()).thenReturn(allMovies);

        // Act
        movieService.getAllMovies();

        // Assert
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    @Order(2)
    public void test_GetAllMovies_WhenCalled_ThenReturnsCorrectListOfMovies() {
        // Mock
        when(movieRepository.findAll()).thenReturn(allMovies);

        // Act
        List<Movie> returnedMovies = movieService.getAllMovies();

        // Assert
        assertThat(returnedMovies).isEqualTo(allMovies);
    }

    @Test
    @Order(3)
    public void test_GetMovieById_WhenIdExists_ThenReturnsCorrectMovie() {
        // Mock
        when(movieRepository.findAll()).thenReturn(allMovies);

        // Act
        Movie returnedMovie = movieService.getMovieById(1L);

        // Assert
        assertThat(returnedMovie).isEqualTo(movie1);
    }

    @Test
    @Order(4)
    public void test_GetMovieById_WhenIdDoesNotExist_ThenReturnsNull() {
        // Mock
        when(movieRepository.findAll()).thenReturn(allMovies);

        // Act
        Movie returnedMovie = movieService.getMovieById(3L);

        // Assert
        assertThat(returnedMovie).isNull();
    }
}