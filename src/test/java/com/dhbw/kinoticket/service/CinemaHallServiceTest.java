package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.entity.CinemaHall;
import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.entity.Seat;
import com.dhbw.kinoticket.repository.CinemaHallRepository;
import com.dhbw.kinoticket.repository.CinemaRepository;
import com.dhbw.kinoticket.repository.SeatRepository;
import com.dhbw.kinoticket.request.CreateSeatRequest;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {CinemaHallServiceTest.class})
public class CinemaHallServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private CinemaHallRepository cinemaHallRepository;

    @Mock
    private CinemaRepository cinemaRepository;

    @Mock
    private CinemaService cinemaService;

    @InjectMocks
    private CinemaHallService cinemaHallService;

    @BeforeEach
    public void setUp() {
        cinemaHallService = new CinemaHallService(seatRepository, cinemaHallRepository, cinemaRepository, cinemaService);
    }

    @Test
    @Order(1)
    void test_getCinemaHallById_WhenFound_ThenReturnCinemaHall() {
        // Arrange
        Long cinemaHallId = 1L;
        String cinemaHallName = "Name 1";
        CinemaHall cinemaHall = new CinemaHall(cinemaHallId, cinemaHallName, null, null);

        // Mock
        when(cinemaHallRepository.findById(cinemaHallId)).thenReturn(Optional.of(cinemaHall));

        // Act
        CinemaHall actualCinemaHall = cinemaHallService.getCinemaHallById(cinemaHallId);

        // Assert
        assertEquals(cinemaHallId, actualCinemaHall.getId());
        assertEquals(cinemaHallName, actualCinemaHall.getName());

    }

    @Test
    @Order(2)
    void test_getCinemaHallById_WhenNotFound_ThenAssertNull() {
        // Arrange
        Long cinemaHallId = 2L;

        when(cinemaHallRepository.findById(cinemaHallId)).thenThrow(new IllegalArgumentException("CinemaHall not found with ID: " + cinemaHallId));

        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            cinemaHallService.getCinemaHallById(cinemaHallId);
        });
    }

    @Test
    @Order(3)
    void test_CreateCinemaHallAndAddToCinema_WhenCalledWithValidArguments() {
        // Arrange
        Long cinemaId = 1L;
        String name = "CinemaHall 1";
        Cinema cinema = new Cinema(cinemaId, "Test cinema 1", new LocationAddress(1L, "Test street 1", "Test city 1", "Test country 1", "11111"), new ArrayList<>(), null);
        CinemaHall cinemaHall = new CinemaHall(1L, name, null, null);

        // Mock behavior for getCinemaById
        when(cinemaService.getCinemaById(cinemaId)).thenReturn(cinema);

        // Mock behavior for save methods
        when(cinemaHallRepository.save(any(CinemaHall.class))).thenReturn(cinemaHall);
        when(cinemaRepository.save(any(Cinema.class))).thenReturn(cinema);

        // Act
        CinemaHall createdCinemaHall = cinemaHallService.createCinemaHallAndAddToCinema(cinemaId, name);

        // Assert
        assertEquals(cinemaHall.getName(), createdCinemaHall.getName());
        assertEquals(cinema, createdCinemaHall.getCinema());
        assertTrue(cinema.getCinemaHallList().contains(createdCinemaHall));

        // Verify that save methods were called
        verify(cinemaHallRepository, times(1)).save(any(CinemaHall.class));
        verify(cinemaRepository, times(1)).save(any(Cinema.class));
    }

    @Test
    @Order(4)
    void test_AddSeatsToCinemaHall_WhenSeatsAdded_ThenCinemaHallUpdated() {
        // Arrange
        Long cinemaHallId = 1L;
        List<CreateSeatRequest> createSeatRequests = new ArrayList<>();
        createSeatRequests.add(new CreateSeatRequest('A', 1, 10, 10, true));
        CinemaHall cinemaHall = new CinemaHall(cinemaHallId, "CinemaHall 1", new ArrayList<>(), null);
        List<CinemaHall> cinemaHallList = new ArrayList<>();
        cinemaHallList.add(cinemaHall);

        // Mock: Return the cinema hall when findById is called
        when(cinemaHallRepository.findById(cinemaHallId)).thenReturn(Optional.of(cinemaHall));
        // Mock the repository save method
        when(cinemaHallRepository.save(any(CinemaHall.class))).thenReturn(cinemaHall);

        // Act
        CinemaHall updatedCinemaHall = cinemaHallService.addSeatsToCinemaHall(cinemaHallId, createSeatRequests);

        // Assert
        assertEquals(cinemaHallId, updatedCinemaHall.getId());
        assertEquals(createSeatRequests.size(), updatedCinemaHall.getSeats().size());
        verify(cinemaHallRepository, times(1)).save(any(CinemaHall.class));
    }

    @Test
    @Order(5)
    void test_AddSeatsToCinemaHall_WhenCinemaHallDoesNotExist_ThenExceptionIsThrown() {
        // Arrange
        Long cinemaHallId = 2L;
        CreateSeatRequest createSeatRequest = new CreateSeatRequest('A', 1, 10, 10, true);

        // Mock - Simulate that the cinema hall with the provided ID is not found
        when(cinemaHallRepository.findById(cinemaHallId)).thenReturn(Optional.empty());

        // Act and Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> cinemaHallService.addSeatsToCinemaHall(cinemaHallId, List.of(createSeatRequest)));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @Order(6)
    void test_updateSeatsOfCinemaHall_WhenCinemaExists_ThenCinemaHallUpdated() {
        // Arrange
        Long cinemaHallId = 1L;
        String newName = "Updated Cinema Hall Name";
        List<CreateSeatRequest> newSeatRequests = new ArrayList<>();

        // Mock behavior to return a cinema hall with seats when getCinemaHallById is called
        CinemaHall existingCinemaHall = new CinemaHall(cinemaHallId, "Cinema Hall Name", null, null);
        List<Seat> oldSeats = new ArrayList<>();
        existingCinemaHall.setSeats(oldSeats);

        // Mock the repository behavior
        when(cinemaHallRepository.findById(cinemaHallId)).thenReturn(Optional.of(existingCinemaHall));

        // Act
        CinemaHall updatedCinemaHall = cinemaHallService.updateSeatsOfCinemaHall(cinemaHallId, newName, newSeatRequests);

        // Assert
        verify(seatRepository, times(oldSeats.size())).delete(any(Seat.class));
        verify(seatRepository, times(newSeatRequests.size())).save(any(Seat.class));
        assertEquals(newName, updatedCinemaHall.getName());
    }

    @Test
    @Order(8)
    void test_DeleteCinemaHall_WhenCinemaHallDoesNotExist_ThenExceptionIsThrown() {
        // Arrange
        Long cinemaHallId = 2L;

        // Act and Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> cinemaHallService.deleteCinemaHall(cinemaHallId));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    @Order(9)
    public void test_ConvertSeatDTOsToSeats_WhenGivenValidInputs_ThenReturnsCorrectSeats() {
        // Arrange
        List<CreateSeatRequest> seatDTOList = new ArrayList<>();
        seatDTOList.add(new CreateSeatRequest('A', 1, 10, 20, false));
        seatDTOList.add(new CreateSeatRequest('B', 2, 15, 25, true));
        CinemaHall cinemaHall = new CinemaHall();

        // Act
        List<Seat> seats = cinemaHallService.convertSeatDTOsToSeats(seatDTOList, cinemaHall);

        // Assert
        assertThat(seats).hasSize(seatDTOList.size());
        for (int i = 0; i < seats.size(); i++) {
            Seat seat = seats.get(i);
            CreateSeatRequest seatDTO = seatDTOList.get(i);
            assertThat(seat.getSeatRow()).isEqualTo(seatDTO.getSeatRow());
            assertThat(seat.getNumber()).isEqualTo(seatDTO.getNumber());
            assertThat(seat.getXLoc()).isEqualTo(seatDTO.getXLoc());
            assertThat(seat.getYLoc()).isEqualTo(seatDTO.getYLoc());
            assertThat(seat.isBlocked()).isEqualTo(seatDTO.isBlocked());
            assertThat(seat.getCinemaHall()).isEqualTo(cinemaHall);
        }
    }

    @Test
    @Order(10)
    public void test_ConvertSeatDTOsToSeats_WhenGivenEmptyList_ThenReturnsEmptySeats() {
        // Arrange
        List<CreateSeatRequest> seatDTOList = new ArrayList<>();
        CinemaHall cinemaHall = new CinemaHall();

        // Act
        List<Seat> seats = cinemaHallService.convertSeatDTOsToSeats(seatDTOList, cinemaHall);

        // Assert
        assertThat(seats).isEmpty();
    }

    @Test
    @Order(11)
    void test_DoesCinemaHallExist_WhenExistsThenTrue() {
        // Arrange
        Long cinemaHallId = 1L;

        // Mock
        when(cinemaHallRepository.existsById(cinemaHallId)).thenReturn(true);

        // Act
        boolean doesExist = cinemaHallService.doesCinemaHallExist(cinemaHallId);

        // Assert
        assertTrue(doesExist);
    }

    @Test
    @Order(12)
    void test_DoesCinemaHallExist_WhenNotExists_ThenFalse() {
        // Arrange
        Long cinemaHallId = 1L;

        // Mock
        when(cinemaRepository.existsById(cinemaHallId)).thenReturn(false);

        // Act
        boolean doesExist = cinemaHallService.doesCinemaHallExist(cinemaHallId);

        // Assert
        assertFalse(doesExist);
    }
}
