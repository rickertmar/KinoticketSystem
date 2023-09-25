package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.CinemaHall;
import com.dhbw.kinoticket.entity.Movie;
import com.dhbw.kinoticket.entity.Seat;
import com.dhbw.kinoticket.entity.Showing;
import com.dhbw.kinoticket.repository.MovieRepository;
import com.dhbw.kinoticket.repository.ShowingRepository;
import com.dhbw.kinoticket.request.CreateShowingRequest;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {ShowingServiceTest.class})
public class ShowingServiceTest {

    @Mock
    private ShowingRepository showingRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private CinemaHallService cinemaHallService;

    @Mock
    private MovieService movieService;

    @InjectMocks
    private ShowingService showingService;

    private Showing showing1;
    private Showing showing2;
    private List<Showing> allShowings;

    @BeforeEach
    void setUp() {
        showing1 = new Showing(1L, 12.70, null, null, "3D", null, null);

        showing2 = new Showing(2L, 9.70, null, null, "2D", null, null);

        allShowings = Arrays.asList(showing1, showing2);
    }


    @Test
    @Order(1)
    public void test_GetAllShowings_WhenInvoked_ThenRepositoryFindAllIsCalled() {
        // Arrange
        List<Showing> expectedShowings = Collections.singletonList(new Showing());
        when(showingRepository.findAll()).thenReturn(expectedShowings);

        // Act
        List<Showing> actualShowings = showingService.getAllShowings();

        // Assert
        verify(showingRepository, times(1)).findAll();
        assertThat(actualShowings).isEqualTo(expectedShowings);
    }

    @Test
    @Order(2)
    public void test_GetShowingById_WhenIdIsValid_ThenReturnsCorrectShowing() {
        // Arrange
        Long id = 1L;
        when(showingRepository.findById(id)).thenReturn(Optional.of(showing1));

        // Act
        Showing actualShowing = showingService.getShowingById(id);

        // Assert
        verify(showingRepository, times(1)).findById(id);
        assertThat(actualShowing).isEqualTo(showing1);
    }

    @Test
    @Order(3)
    public void test_GetShowingById_WhenIdDoesNotExist_ThenThrowsIllegalArgumentException() {
        // Arrange
        Long id = 1L;
        when(showingRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> showingService.getShowingById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Showing not found with ID: " + id);
        verify(showingRepository, times(1)).findById(id);
    }

    @Test
    @Order(4)
    public void test_CreateShowing_WhenRequestIsValid_ThenReturnsShowing() {
        // Arrange
        CreateShowingRequest request = new CreateShowingRequest();
        request.setMovieId(1L);
        request.setCinemaHallId(1L);
        request.setTime(LocalDateTime.now());
        request.setShowingExtras("3D");
        CinemaHall cinemaHall = new CinemaHall();
        cinemaHall.setSeats(Arrays.asList(new Seat(), new Seat()));

        // Mock
        when(movieService.getMovieById(request.getMovieId())).thenReturn(null);
        when(cinemaHallService.getCinemaHallById(request.getCinemaHallId())).thenReturn(null);
        when(cinemaHallService.getCinemaHallById(request.getCinemaHallId())).thenReturn(cinemaHall);
        when(showingRepository.save(any(Showing.class))).thenReturn(showing1);

        // Act
        Showing actualShowing = showingService.createShowing(request);

        // Assert
        verify(showingRepository, times(1)).save(any(Showing.class));
        assertThat(actualShowing).isEqualTo(showing1);
    }

    @Test
    @Order(5)
    public void test_UpdateShowing_WhenValidRequest_ThenShowingUpdated() {
        // Arrange
        CreateShowingRequest request = CreateShowingRequest.builder()
                .movieId(1L)
                .cinemaHallId(1L)
                .time(LocalDateTime.now())
                .showingExtras("3D")
                .build();

        Showing existingShowing = Showing.builder()
                .id(1L)
                .build();

        // Mock
        when(showingRepository.findById(1L)).thenReturn(Optional.of(existingShowing));
        when(movieService.getMovieById(request.getMovieId())).thenReturn(new Movie());
        when(cinemaHallService.getCinemaHallById(request.getCinemaHallId())).thenReturn(new CinemaHall());
        when(showingRepository.save(any(Showing.class))).thenAnswer(invocation -> {
            Showing savedShowing = invocation.getArgument(0);
            savedShowing.setId(1L);
            return savedShowing;
        });

        // Act
        Showing updatedShowing = showingService.updateShowing(1L, request);

        // Assert
        verify(showingRepository, times(1)).save(any(Showing.class));
        assertThat(updatedShowing).isNotNull();
        assertThat(updatedShowing.getMovie()).isNotNull();
        assertThat(updatedShowing.getCinemaHall()).isNotNull();
        assertThat(updatedShowing.getTime()).isEqualTo(request.getTime());
        assertThat(updatedShowing.getShowingExtras()).isEqualTo(request.getShowingExtras());
    }

    @Test
    @Order(6)
    public void test_UpdateShowing_WhenShowingDoesNotExist_ThenExceptionThrown() {
        // Arrange
        CreateShowingRequest request = CreateShowingRequest.builder()
                .movieId(1L)
                .cinemaHallId(1L)
                .time(LocalDateTime.now())
                .showingExtras("3D")
                .build();

        // Mock
        when(showingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> showingService.updateShowing(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Showing not found with ID: " + 1L);
        verify(showingRepository, times(1)).findById(1L);
    }

    @Test
    @Order(7)
    public void test_DeleteShowing_WhenShowingExists_ThenDeleteIsCalled() {
        // Arrange
        Long id = 1L;

        // Mock
        when(showingRepository.findById(id)).thenReturn(Optional.of(showing1));

        // Act
        showingService.deleteShowing(id);

        // Assert
        verify(showingRepository, times(1)).delete(showing1);
    }

    @Test
    @Order(8)
    public void test_DeleteShowing_WhenShowingDoesNotExist_ThenExceptionIsThrown() {
        // Arrange
        Long id = 1L;

        // Mock
        when(showingRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> showingService.deleteShowing(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Showing not found with ID: " + id);
        verify(showingRepository, times(1)).findById(id);
    }

    @Test
    @Order(9)
    public void test_GetSeatsOfCinemaHall_WhenCinemaHallHasSeats_ThenReturnSetOfSeats() {
        // Arrange
        CinemaHall cinemaHall = new CinemaHall();
        cinemaHall.setId(1L);
        cinemaHall.setName("Hall 1");

        Seat seat1 = Seat.builder()
                .id(1L)
                .seatRow('A')
                .number(1)
                .xLoc(10)
                .yLoc(5)
                .isBlocked(false)
                .cinemaHall(null)
                .build();

        cinemaHall.setSeats(Arrays.asList(seat1));

        Set<Seat> seatSet = new HashSet<>();
        seatSet.add(seat1);

        Long id = 1L;

        //Mock
        when(cinemaHallService.getCinemaHallById(id)).thenReturn(cinemaHall);

        // Act
        Set<Seat> actualSeatSet = showingService.getSeatsOfCinemaHall(id);

        // Assert
        verify(cinemaHallService, times(1)).getCinemaHallById(id);
        assertThat(actualSeatSet).isEqualTo(seatSet);
    }

    @Test
    @Order(10)
    public void test_GetSeatsOfCinemaHall_WhenCinemaHallHasNoSeats_ThenReturnEmptySet() {
        // Arrange
        Long id = 1L;
        CinemaHall cinemaHall = new CinemaHall();
        cinemaHall.setSeats(Collections.emptyList());
        when(cinemaHallService.getCinemaHallById(id)).thenReturn(cinemaHall);

        // Act
        Set<Seat> actualSeatSet = showingService.getSeatsOfCinemaHall(id);

        // Assert
        verify(cinemaHallService, times(1)).getCinemaHallById(id);
        assertThat(actualSeatSet).isEmpty();
    }

    @Test
    @Order(11)
    public void test_DoesMovieExist_WhenMovieIdExists_ThenReturnTrue() {
        // Arrange
        Long id = 1L;
        when(movieRepository.existsById(id)).thenReturn(true);

        // Act
        boolean doesExist = showingService.doesMovieExist(id);

        // Assert
        verify(movieRepository, times(1)).existsById(id);
        assertThat(doesExist).isTrue();
    }

    @Test
    @Order(12)
    public void test_DoesMovieExist_WhenMovieIdDoesNotExist_ThenReturnFalse() {
        // Arrange
        Long id = 1L;
        when(movieRepository.existsById(id)).thenReturn(false);

        // Act
        boolean doesExist = showingService.doesMovieExist(id);

        // Assert
        verify(movieRepository, times(1)).existsById(id);
        assertThat(doesExist).isFalse();
    }
}