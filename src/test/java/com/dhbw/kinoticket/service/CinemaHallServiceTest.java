package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.entity.CinemaHall;
import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.entity.Seat;
import com.dhbw.kinoticket.repository.CinemaHallRepository;
import com.dhbw.kinoticket.repository.CinemaRepository;
import com.dhbw.kinoticket.repository.SeatRepository;
import com.dhbw.kinoticket.request.CreateSeatRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CinemaHallServiceTest.class})
class CinemaHallServiceTest {

    @Mock
    private CinemaHallRepository cinemaHallRepository;
    @Mock
    private CinemaRepository cinemaRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private CinemaService cinemaService;
    @InjectMocks
    private CinemaHallService cinemaHallService;

    @BeforeEach
    void setUp() {
        cinemaHallService = new CinemaHallService(seatRepository, cinemaHallRepository, cinemaRepository, cinemaService);
    }

    @Test
    void test_getCinemaHallById_Found() {
        // Arrange
        Long cinemaHallId = 1L;
        String cinemaHallName = "Name 1";
        List<CinemaHall> myCinemaHalls = new ArrayList<>();
        myCinemaHalls.add(new CinemaHall(1L, "Name 1", null, null));
        myCinemaHalls.add(new CinemaHall(2L, "Name 2", null, null));
        myCinemaHalls.add(new CinemaHall(3L, "Name 3", null, null));

        // Mock
        when(cinemaHallRepository.findAll()).thenReturn(myCinemaHalls);

        // Act
        CinemaHall actualCinemaHall = cinemaHallService.getCinemaHallById(cinemaHallId);

        // Assert
        assertEquals(cinemaHallId, actualCinemaHall.getId());
        assertEquals(cinemaHallName, actualCinemaHall.getName());
    }

    @Test
    void test_getCinemaHallById_NotFound() {
        // Arrange
        Long cinemaHallId = 2L;
        List<CinemaHall> cinemaHalls = new ArrayList<>();

        // Mock
        when(cinemaHallRepository.findAll()).thenReturn(cinemaHalls);

        // Act
        CinemaHall actualCinemaHall = cinemaHallService.getCinemaHallById(cinemaHallId);

        // Assert
        assertNull(actualCinemaHall);
    }

    @Test
    void test_createCinemaHallAndAddToCinema() {
        // Arrange
        Long cinemaId = 1L;
        String name = "CinemaHall 1";
        Cinema cinema = new Cinema(cinemaId, "Test cinema 1", new LocationAddress(1L, "Test street 1", "Test city 1", "Test country 1", "11111"), new ArrayList<>());
        CinemaHall cinemaHall = new CinemaHall(1L, name,null, null);

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
    void test_addSeatsToCinemaHall() {
        // Arrange
        Long cinemaHallId = 1L;
        String name = "CinemaHall 1";
        List<CreateSeatRequest> createSeatRequests = new ArrayList<>();
        createSeatRequests.add(new CreateSeatRequest('A', 1, 10, 10, true));
        createSeatRequests.add(new CreateSeatRequest('A', 2, 20, 10, false));
        createSeatRequests.add(new CreateSeatRequest('A', 3, 30, 10, true));

        CinemaHall cinemaHall = new CinemaHall(cinemaHallId, name,null, null);
        List<Seat> seatsToAdd = new ArrayList<>();
        seatsToAdd.add(new Seat(1L,'A', 1, 10, 10, true, null));
        seatsToAdd.add(new Seat(2L,'A', 2, 20, 10, false, null));
        seatsToAdd.add(new Seat(3L,'A', 3, 30, 10, true, null));
        cinemaHall.setSeats(seatsToAdd);

        List<CinemaHall> cinemaHallList = new ArrayList<>();
        cinemaHallList.add(cinemaHall);

        // Mock
        when(cinemaHallRepository.findAll()).thenReturn(cinemaHallList);
        when(cinemaHallRepository.save(any(CinemaHall.class))).thenReturn(cinemaHall);

        // Act
        CinemaHall updatedCinemaHall = cinemaHallService.addSeatsToCinemaHall(cinemaHallId, createSeatRequests);

        // Assert
        assertEquals(cinemaHall, updatedCinemaHall);
    }

    @Test
    void test_updateSeatsOfCinemaHall() {
        // Arrange
        Long cinemaHallId = 1L;
        String newName = "Updated Cinema Hall Name";
        List<CreateSeatRequest> newSeatRequests = new ArrayList<>(); // Replace with your seat request data

        // Mock behavior to return a cinema hall with seats when getCinemaHallById is called
        CinemaHall existingCinemaHall = new CinemaHall(cinemaHallId, "Cinema Hall Name", null, null);
        List<Seat> oldSeats = new ArrayList<>(); // Replace with your old seat data
        existingCinemaHall.setSeats(oldSeats);
        List<CinemaHall> cinemaHallList = new ArrayList<>();
        cinemaHallList.add(existingCinemaHall);
        when(cinemaHallRepository.findAll()).thenReturn(cinemaHallList);

        // Mock behavior for seatRepository.save and seatRepository.delete
        when(seatRepository.save(any(Seat.class))).thenReturn(null);

        // Act
        CinemaHall updatedCinemaHall = cinemaHallService.updateSeatsOfCinemaHall(cinemaHallId, newName, newSeatRequests);

        // Assert
        verify(seatRepository, times(oldSeats.size())).delete(any(Seat.class));
        verify(seatRepository, times(newSeatRequests.size())).save(any(Seat.class));
        assertEquals(newName, updatedCinemaHall.getName());
    }

    @Test
    @Disabled // cinemaHall.getCinema() throws NullPointerException because it is not mocked
    void test_deleteCinemaHall_Success() {
        // Arrange
        Long cinemaHallId = 1L;

        // Create a valid CinemaHall object with a valid Cinema association
        CinemaHall cinemaHall = new CinemaHall(cinemaHallId, "CinemaHall 1", null, null);
        Cinema cinema = new Cinema(1L, "Test cinema 1", null, new ArrayList<>());
        cinema.getCinemaHallList().add(cinemaHall);

        // Mock behavior to return the CinemaHall with a valid Cinema
        when(cinemaHallRepository.findById(cinemaHallId)).thenReturn(Optional.of(cinemaHall));

        // Mock behavior to return cinema by ID
        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(cinema));

        when(cinemaHall.getCinema()).thenReturn(cinema);

        // Act
        cinemaHallService.deleteCinemaHall(cinemaHallId);

        // Assert
        verify(cinema.getCinemaHallList(), times(1)).remove(cinemaHall);
        verify(cinemaRepository, times(1)).save(cinema);
        verify(cinemaHallRepository, times(1)).delete(cinemaHall);
    }

    @Test
    public void testDeleteCinemaHall_Error() {
        // Arrange
        Long cinemaHallId = 2L; // A cinema hall ID that does not exist

        // Mock behavior to throw an exception when trying to delete
        when(cinemaHallRepository.findById(cinemaHallId)).thenReturn(Optional.empty());

        // Act and Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> cinemaHallService.deleteCinemaHall(cinemaHallId));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    void test_convertSeatDTOsToSeats() {
        // Arrange
        List<CreateSeatRequest> seatDTOList = new ArrayList<>();
        seatDTOList.add(new CreateSeatRequest('A', 1, 10, 20, false));
        seatDTOList.add(new CreateSeatRequest('B', 2, 15, 25, true));

        CinemaHall cinemaHall = mock(CinemaHall.class);

        // Act
        List<Seat> seats = cinemaHallService.convertSeatDTOsToSeats(seatDTOList, cinemaHall);

        // Assert
        assertEquals(2, seats.size());

        // Verify the properties of the first seat
        Seat firstSeat = seats.get(0);
        assertEquals('A', firstSeat.getSeatRow());
        assertEquals(1, firstSeat.getNumber());
        assertEquals(10, firstSeat.getXLoc());
        assertEquals(20, firstSeat.getYLoc());
        assertFalse(firstSeat.isBlocked());
        assertEquals(cinemaHall, firstSeat.getCinemaHall());

        // Verify the properties of the second seat
        Seat secondSeat = seats.get(1);
        assertEquals('B', secondSeat.getSeatRow());
        assertEquals(2, secondSeat.getNumber());
        assertEquals(15, secondSeat.getXLoc());
        assertEquals(25, secondSeat.getYLoc());
        assertTrue(secondSeat.isBlocked());
        assertEquals(cinemaHall, secondSeat.getCinemaHall());
    }
}