package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.repository.CinemaRepository;
import com.dhbw.kinoticket.repository.LocationAddressRepository;
import com.dhbw.kinoticket.request.CreateCinemaRequest;
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

@SpringBootTest(classes = {CinemaServiceTest.class})
class CinemaServiceTest {

    @Mock
    private CinemaRepository cinemaRepository;
    @Mock
    private LocationAddressRepository locationAddressRepository;
    @InjectMocks
    private CinemaService cinemaService;

    @BeforeEach
    void setUp() {
        cinemaService = new CinemaService(cinemaRepository, locationAddressRepository);
    }


    @Test
    void test_GetCinemaById_WhenIdIsValid_ThenReturnCinema() {
        // Arrange
        Long cinemaId = 1L;
        Cinema cinema = new Cinema(cinemaId, "Test cinema 1", new LocationAddress(1L, "Test street 1", "Test city 1", "Test country 1", "11111"), new ArrayList<>());
        List<Cinema> cinemaList = new ArrayList<>();
        cinemaList.add(cinema);

        // Mock
        when(cinemaRepository.findAll()).thenReturn(cinemaList);

        // Act
        Cinema result = cinemaService.getCinemaById(cinemaId);

        // Assert
        assertEquals(cinema, result);
    }

    @Test
    void test_GetCinemaById_WhenIdIsInvalid_ThenReturnNull() {
        // Arrange
        Long cinemaId = 2L;
        List<Cinema> cinemaList = new ArrayList<>();

        // Mock
        when(cinemaRepository.findAll()).thenReturn(cinemaList);

        // Act
        Cinema result = cinemaService.getCinemaById(cinemaId);

        // Assert
        assertNull(result);
    }

    @Test
    void test_GetAllCinemas_WhenAllCinemasFound_ThenSuccess() {
        // Arrange
        List<Cinema> myCinemas = new ArrayList<Cinema>();
        myCinemas.add(new Cinema(1L, "Test cinema 1", new LocationAddress(1L, "Test street 1", "Test city 1", "Test country 1", "11111"), new ArrayList<>()));
        myCinemas.add(new Cinema(2L, "Test cinema 2", new LocationAddress(2L, "Test street 2", "Test city 2", "Test country 2", "22222"), new ArrayList<>()));
        myCinemas.add(new Cinema(3L, "Test cinema 3", new LocationAddress(3L, "Test street 3", "Test city 3", "Test country 3", "33333"), new ArrayList<>()));

        // Mock behavior of cinemaRepository.findAll used in cinemaService.getAllCinemas
        when(cinemaRepository.findAll()).thenReturn(myCinemas); //Mocking

        // Act and Assert
        assertEquals(3, cinemaService.getAllCinemas().size());
    }

    @Test
    void test_GetAllCinemas_WhenNoCinemasFound_ThenListIsEmpty() {
        // Arrange
        List<Cinema> myCinemas = new ArrayList<Cinema>();

        // Mock behavior of cinemaRepository.findAll used in cinemaService.getAllCinemas
        when(cinemaRepository.findAll()).thenReturn(myCinemas); //Mocking

        // Act and Assert
        assertEquals(0, cinemaService.getAllCinemas().size());
    }

    @Test
    void test_CreateCinema_WhenValidRequest_ThenCinemaCreated() {
        // Arrange
        CreateCinemaRequest request = new CreateCinemaRequest("Test cinema", "Test street", "Test city", "Test country", "11111");
        LocationAddress locationAddress = new LocationAddress(1L, "Test street", "Test city", "Test country", "11111");
        Cinema cinema = new Cinema(1L, "Test cinema", locationAddress, new ArrayList<>());

        // Mock
        when(locationAddressRepository.save(any(LocationAddress.class))).thenReturn(locationAddress);
        when(cinemaRepository.save(any(Cinema.class))).thenReturn(cinema);

        // Act
        Cinema result = cinemaService.createCinema(request);

        // Assert
        verify(locationAddressRepository, times(1)).save(any(LocationAddress.class));
        verify(cinemaRepository, times(1)).save(any(Cinema.class));
        assertEquals(cinema.getName(), result.getName());
    }

    @Test
    void test_CreateCinema_WhenNullFields_ThenRepositoriesNotCalled() {
        // Arrange
        CreateCinemaRequest request = new CreateCinemaRequest(null, null, null, null, null);

        // Act
        Cinema result = cinemaService.createCinema(request);

        // Assert
        verify(locationAddressRepository, times(0)).save(any(LocationAddress.class));
        verify(cinemaRepository, times(0)).save(any(Cinema.class));
        assertNull(result);
    }

    @Test
    void test_UpdateCinema_WhenCinemaExists_ThenCinemaIsUpdated() {
        // Arrange
        Long cinemaId = 1L;
        String updatedName = "Updated Cinema";
        CreateCinemaRequest updateRequest = new CreateCinemaRequest();
        updateRequest.setName(updatedName);
        updateRequest.setStreet("Updated Street");
        updateRequest.setCity("Updated City");
        updateRequest.setCountry("Updated Country");
        updateRequest.setPostcode("Updated Postcode");

        Cinema existingCinema = new Cinema(cinemaId, "Test cinema 1", new LocationAddress(1L, "Test street", "Test city", "Test country", "11111"), new ArrayList<>());
        List<Cinema> cinemaList = new ArrayList<>();
        cinemaList.add(existingCinema);

        // Mock
        when(cinemaRepository.findAll()).thenReturn(cinemaList);
        when(cinemaRepository.save(any(Cinema.class))).thenReturn(existingCinema);

        // Act
        Cinema updatedCinema = cinemaService.updateCinema(cinemaId, updateRequest);

        // Assert
        verify(cinemaRepository, times(1)).save(existingCinema);
        assertEquals(updatedName, updatedCinema.getName());
    }

    @Test
    void test_UpdateCinema_WhenCinemaDoesNotExist_ThenExceptionIsThrown() {
        // Arrange
        Long cinemaId = 2L;
        CreateCinemaRequest updateRequest = new CreateCinemaRequest();
        updateRequest.setName("Updated Cinema");
        updateRequest.setStreet("Updated Street");
        updateRequest.setCity("Updated City");
        updateRequest.setCountry("Updated Country");
        updateRequest.setPostcode("Updated Postcode");

        // Mock
        when(cinemaRepository.findAll()).thenReturn(new ArrayList<>());

        // Act and Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> cinemaService.updateCinema(cinemaId, updateRequest));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void test_DeleteCinema_Success() {
        // Arrange
        LocationAddress location = new LocationAddress(3L, "Test street 3", "Test city 3", "Test country 3", "33333");
        Cinema cinema = new Cinema(3L, "Test cinema 3", location, new ArrayList<>());
        Long id = 1L;

        // Mock
        when(cinemaRepository.findById(id)).thenReturn(Optional.of(cinema));

        // Act
        cinemaService.deleteCinema(id);

        // Assert
        verify(cinemaRepository, times(1)).delete(cinema); //verify instead of assert, because there is no return value
    }

    @Test
    void test_DeleteCinema_WhenCinemaNotFound_ThenExceptionIsThrown() {
        // Arrange
        Long cinemaId = 2L;

        // Mock behavior of cinemaRepository.findById to return an empty optional
        when(cinemaRepository.findById(cinemaId)).thenReturn(Optional.empty());

        // Act and Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> cinemaService.deleteCinema(cinemaId));

        // Verify the HttpStatus code of the exception
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    @Disabled
        // Testing needed? GetCinema and save is already tested
    void test_UpdateLocationAddress() {
    }

    @Test
    void test_UpdateLocationAddress_WhenInvalidId_ThenExceptionThrown() {
        // Arrange
        Long cinemaId = 2L;
        LocationAddress newLocationAddress = new LocationAddress(2L, "New street", "New city", "New country", "22222");

        // Mock
        when(cinemaRepository.findById(cinemaId)).thenReturn(Optional.empty());

        // Act and Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> cinemaService.updateLocationAddress(cinemaId, newLocationAddress));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}