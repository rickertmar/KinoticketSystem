package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.entity.FSK;
import com.dhbw.kinoticket.entity.Movie;
import com.dhbw.kinoticket.repository.CinemaRepository;
import com.dhbw.kinoticket.repository.MovieRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {MovieServiceTest.class})
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private CinemaRepository cinemaRepository;

    @Mock
    private CinemaService cinemaService;

    @InjectMocks
    private MovieService movieService;

    private Movie movie1;
    private Movie movie2;
    private List<Movie> allMovies;
    private Cinema cinema;

    @BeforeEach
    public void setUp() {
        cinema = new Cinema();
        cinema.setId(1L);
        cinema.setName("Cinema 1");

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
                cinema
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
        // Arrange
        Long id = 1L;

        // Mock
        when(movieRepository.findById(id)).thenReturn(Optional.ofNullable(movie1));

        // Act
        Movie returnedMovie = movieService.getMovieById(1L);

        // Assert
        assertThat(returnedMovie).isEqualTo(movie1);
    }

    @Test
    @Order(4)
    public void test_GetMovieById_WhenIdDoesNotExist_ThenReturnsNull() {
        // Arrange
        Long id = 1L;

        // Mock
        when(movieRepository.findById(id)).thenReturn(Optional.ofNullable(movie1));

        // Act & Assert
        assertThatThrownBy(() -> movieService.getMovieById(3L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Movie not found with ID: 3");
    }

    @Test
    @Order(5)
    public void test_AddMovieToCinema_WhenMovieAndCinemaAreValid_ThenMovieIsAdded() {
        // Arrange
        when(cinemaService.getCinemaById(1L)).thenReturn(cinema);

        // Act
        Movie returnedMovie = movieService.addMovieToCinema(1L, movie1);

        // Assert
        assertThat(cinema.getMovieList()).contains(movie1);
        assertThat(returnedMovie).isEqualTo(movie1);
        verify(cinemaRepository, times(1)).save(cinema);
    }

    @Test
    @Order(6)
    public void test_AddMovieToCinema_WhenCinemaNotFound_ThenExceptionIsThrown() {
        // Arrange
        when(cinemaService.getCinemaById(1L)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> movieService.addMovieToCinema(1L, movie1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cinema not found with ID: 1");
    }

    @Test
    @Order(7)
    public void test_AddMovieToCinema_WhenMovieDataIsInvalid_ThenExceptionIsThrown() {
        // Arrange
        when(cinemaService.getCinemaById(1L)).thenReturn(cinema);

        // Act & Assert
        assertThatThrownBy(() -> movieService.addMovieToCinema(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid or missing movie data");
    }

    @Test
    @Order(8)
    public void test_RemoveMovie_WhenMovieIdIsValid_ThenMovieIsRemoved() {
        // Arrange
        when(movieRepository.findById(movie2.getId())).thenReturn(Optional.of(movie2));
        when(cinemaRepository.save(cinema)).thenReturn(cinema);

        // Act
        movieService.removeMovie(movie2.getId());

        // Assert
        assertThat(cinema.getMovieList()).doesNotContain(movie2);
        verify(cinemaRepository, times(1)).save(cinema);
        verify(movieRepository, times(1)).delete(movie2);
    }

    @Test
    @Order(9)
    public void test_RemoveMovie_WhenMovieIdIsInvalid_ThenExceptionIsThrown() {
        // Arrange
        Long invalidId = 3L;

        // Act & Assert
        assertThatThrownBy(() -> movieService.removeMovie(invalidId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Movie not found with ID: " + invalidId);
    }
}