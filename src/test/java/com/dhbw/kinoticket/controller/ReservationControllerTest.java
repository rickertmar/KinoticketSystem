package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.Discount;
import com.dhbw.kinoticket.entity.Movie;
import com.dhbw.kinoticket.entity.Ticket;
import com.dhbw.kinoticket.entity.User;
import com.dhbw.kinoticket.request.CreateReservationRequest;
import com.dhbw.kinoticket.response.ReservationResponse;
import com.dhbw.kinoticket.response.WorkerReservationResponse;
import com.dhbw.kinoticket.service.ReservationService;
import com.dhbw.kinoticket.service.TicketService;
import com.dhbw.kinoticket.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan(basePackages = "com.dhbw.kinoticket")
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(classes = {ReservationControllerTest.class})
class ReservationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    private ReservationService reservationService;

     @Mock
     private TicketService ticketService;

     @Mock
     private UserService userService;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
    }

    @Test
    @Order(1)
    void test_getWorkerReservationById_ShouldReturnWorkerReservationResponse() throws Exception {
        // Arrange
        Long reservationId = 1l;
        WorkerReservationResponse response = new WorkerReservationResponse(1L, 10.0, true, null, null, null, null);

        // Mock
        when(reservationService.getWorkerReservationById(reservationId)).thenReturn(response);

        // Act and Assert
        this.mockMvc.perform(get("/reservation/id/{id}", reservationId))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(print());
    }

    @Test
    @Disabled // Actual 500 TODO
    public void test_CreateReservation_WhenValidRequest_ThenReturnOk() throws Exception {
        // Arrange
        CreateReservationRequest request = new CreateReservationRequest();
        request.setSelectedSeatIdList(List.of(1L, 2L));
        request.setDiscountList(List.of(Discount.STUDENT, Discount.CHILD));
        request.setPaid(true);
        request.setShowingId(1L);

        User user = new User();
        user.setEmail("test@example.com");

        ReservationResponse response = new ReservationResponse();
        response.setMovie(new Movie());
        response.setTickets(new ArrayList<>());
        response.setTotal(10.0);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.getUserByEmail(principal.getName())).thenReturn(user);
        when(reservationService.createReservation(any(CreateReservationRequest.class), any(User.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.movie").exists())
                .andExpect(jsonPath("$.tickets").isArray())
                .andExpect(jsonPath("$.total").value(response.getTotal()));
    }


    // ----------------------------------------------------------------
    // Ticket Testing Methods
    // ----------------------------------------------------------------

    @Test
    //@Order()
    public void test_GetAllTickets_WhenValidRequest_ThenReturnFound() throws Exception {
        // Arrange
        List<Ticket> tickets = Arrays.asList(
                new Ticket(1L, null, Discount.REGULAR, true, null)
        );

        when(ticketService.getAllTickets()).thenReturn(tickets);

        // Act & Assert
        mockMvc.perform(get("/reservation/tickets"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andDo(print());
    }

    @Test
    //@Order()
    public void test_GetTicketById_WhenValidRequest_ThenReturnFound() throws Exception {
        // Arrange
        Long ticketId = 1L;
        Ticket ticket = new Ticket(ticketId, null, Discount.REGULAR, true, null);

        // Mock
        when(ticketService.getTicketById(ticketId)).thenReturn(ticket);

        // Act & Assert
        mockMvc.perform(get("/reservation/tickets/{ticketId}", ticketId))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(print());
    }


    // Helper method to convert an object to JSON string
    private static String asJsonString(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}