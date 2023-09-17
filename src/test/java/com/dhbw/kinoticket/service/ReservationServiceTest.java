package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Reservation;
import com.dhbw.kinoticket.repository.ReservationRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {ReservationServiceTest.class})
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ShowingService showingService;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(reservationRepository, showingService, ticketService);
    }

    @Test
    @Order(1)
    public void test_GetWorkerReservationById_WhenIdExists_ThenReturnReservation() {
        // Arrange
        Reservation reservation1 = new Reservation();
        reservation1.setId(1L);
        Reservation reservation2 = new Reservation();
        reservation2.setId(2L);
        List<Reservation> reservations = Arrays.asList(reservation1, reservation2);

        // Mock
        when(reservationRepository.findAll()).thenReturn(reservations);

        // Act
        var result = reservationService.getWorkerReservationById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @Order(2)
    public void test_GetWorkerReservationById_WhenIdDoesNotExist_ThenReturnNull() {
        // Arrange
        Reservation reservation1 = new Reservation();
        reservation1.setId(1L);
        Reservation reservation2 = new Reservation();
        reservation2.setId(2L);
        List<Reservation> reservations = Arrays.asList(reservation1, reservation2);

        // Mock
        when(reservationRepository.findAll()).thenReturn(reservations);

        // Act
        var result = reservationService.getWorkerReservationById(3L);

        // Assert
        assertNull(result);
    }

    @Test
    @Order(3)
    public void test_ConvertToWorkerResponse_WhenInputIsNull_ThenReturnsNull() {
        // Arrange
        Reservation reservation = null;

        // Act
        var result = reservationService.convertToWorkerResponse(reservation);

        // Assert
        assertNull(result);
    }

    @Test
    @Order(4)
    public void test_ConvertToWorkerResponse_WhenInputIsNonNull_ThenReturnsCorrectResponse() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setTotal(100.0);
        reservation.setPaid(true);

        // Act
        var result = reservationService.convertToWorkerResponse(reservation);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100.0, result.getTotal());
        assertTrue(result.isPaid());
    }
}