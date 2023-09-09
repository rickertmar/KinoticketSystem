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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CinemaServiceTest.class})
class CinemaServiceTest {

    @Mock
    private CinemaRepository cinemaRepository;
    @Mock
    private LocationAddressRepository locationAddressRepository;
    @InjectMocks
    private CinemaService cinemaService;

    public List<Cinema> myCinemas;

    @BeforeEach
    void setUp() {
        cinemaService = new CinemaService(cinemaRepository, locationAddressRepository);
    }

    @Test
    void test_GetAllCinemas() {
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
    void test_GetCinemaById_Found() {
        //Arrange
        List<Cinema> myCinemas = new ArrayList<Cinema>();
        myCinemas.add(new Cinema(1L, "Test cinema 1", new LocationAddress(1L, "Test street 1", "Test city 1", "Test country 1", "11111"), new ArrayList<>()));
        myCinemas.add(new Cinema(2L, "Test cinema 2", new LocationAddress(2L, "Test street 2", "Test city 2", "Test country 2", "22222"), new ArrayList<>()));
        myCinemas.add(new Cinema(3L, "Test cinema 3", new LocationAddress(3L, "Test street 3", "Test city 3", "Test country 3", "33333"), new ArrayList<>()));
        Long cinemaId = 1L;

        // Mock
        when(cinemaRepository.findAll()).thenReturn(myCinemas);

        // Act and Assert
        assertEquals(cinemaId, cinemaService.getCinemaById(cinemaId).getId());
    }

    @Test
    void test_CreateCinema() {
        // Arrange
        LocationAddress location = new LocationAddress(3L, "Test street 3", "Test city 3", "Test country 3", "33333");
        CreateCinemaRequest cinemaRequest = new CreateCinemaRequest("Test cinema 3", "Test street 3", "Test city 3", "Test country 3", "33333");
        Cinema cinema = new Cinema(3L, "Test cinema 3", location, new ArrayList<>());

        // Mock
        when(cinemaRepository.save(cinema)).thenReturn(cinema);

        // Act and Assert
        assertEquals(cinema.getName(), cinemaService.createCinema(cinemaRequest).getName());
    }

    @Test
    void test_UpdateCinema_Success() {
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
    public void testDeleteCinema_CinemaNotFound() {
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
    @Disabled // Testing needed? GetCinema and save is already tested
    void test_UpdateLocationAddress() {
    }
}