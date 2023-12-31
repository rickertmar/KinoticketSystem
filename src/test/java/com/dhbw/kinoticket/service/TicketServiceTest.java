package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Discount;
import com.dhbw.kinoticket.entity.Reservation;
import com.dhbw.kinoticket.entity.Seat;
import com.dhbw.kinoticket.entity.Ticket;
import com.dhbw.kinoticket.repository.TicketRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {TicketServiceTest.class})
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    private List<Ticket> ticketList;

    @BeforeEach
    public void setUp() {
        Ticket ticket1 = new Ticket(1L, null, Discount.REGULAR, true, null);
        Ticket ticket2 = new Ticket(2L, null, Discount.CHILD, true, null);
        ticketList = Arrays.asList(ticket1, ticket2);
    }

    @Test
    @Order(1)
    public void test_GetAllTickets_WhenCalled_ThenCorrectListOfTicketsIsReturned() {
        // Arrange
        when(ticketRepository.findAll()).thenReturn(ticketList);

        // Act
        List<Ticket> returnedTickets = ticketService.getAllTickets();

        // Assert
        verify(ticketRepository, times(1)).findAll();
        assertEquals(ticketList, returnedTickets);
    }

    @Test
    @Order(2)
    public void test_GetTicketById_WhenIdIsValid_ThenReturnTicket() {
        // Arrange
        Ticket expectedTicket = ticketList.get(0);

        // Mock
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(expectedTicket));

        // Act
        Ticket actualTicket = ticketService.getTicketById(1L);

        // Assert
        verify(ticketRepository, times(1)).findById(1L);
        assertEquals(expectedTicket, actualTicket);
    }

    @Test
    @Order(3)
    public void test_GetTicketById_WhenIdIsInvalid_ThenThrowIllegalArgumentException() {
        // Mock
        when(ticketRepository.findById(3L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ticketService.getTicketById(3L));
        assertEquals("Ticket not found with ID: 3", exception.getMessage());
    }

    @Test
    @Order(4)
    public void test_CreateTicket_WhenValidInputs_ThenReturnTicket() {
        // Arrange
        Discount discount = Discount.REGULAR;
        Reservation reservation = new Reservation();
        Seat seat = new Seat();
        seat.setBlocked(true);

        // Act
        Ticket actualTicket = ticketService.createTicket(discount, reservation, seat);

        // Assert
        assertEquals(discount, actualTicket.getDiscount());
        assertEquals(reservation, actualTicket.getReservation());
        assertEquals(seat, actualTicket.getSeat());
    }

    @Test
    @Order(5)
    public void test_CreateTicket_WhenAnyInputs_ThenIsValidTrue() {
        // Arrange
        Discount discount = Discount.REGULAR;
        Reservation reservation = new Reservation();
        Seat seat = new Seat();

        // Act
        Ticket actualTicket = ticketService.createTicket(discount, reservation, seat);

        // Assert
        assertTrue(actualTicket.isValid());
    }
}