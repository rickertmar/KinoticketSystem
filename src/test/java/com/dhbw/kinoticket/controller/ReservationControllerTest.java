package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.Ticket;
import com.dhbw.kinoticket.response.WorkerReservationResponse;
import com.dhbw.kinoticket.service.ReservationService;
import com.dhbw.kinoticket.service.TicketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andDo(print());
    }


    // ----------------------------------------------------------------
    // Ticket Testing Methods
    // ----------------------------------------------------------------

    @Test
    //@Order()
    public void test_GetAllTickets_WhenValidRequest_ThenReturnFound() throws Exception {
        // Arrange
        List<Ticket> tickets = Arrays.asList(
                new Ticket(1L, null, null, 12.70, false, false, null, null)
        );

        when(ticketService.getAllTickets()).thenReturn(tickets);

        // Act & Assert
        mockMvc.perform(get("/reservation/tickets"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andDo(print());
    }

    @Test
    //@Order()
    public void test_GetTicketById_WhenValidRequest_ThenReturnFound() throws Exception {
        // Arrange
        Long ticketId = 1L;
        Ticket ticket = new Ticket(ticketId, null, null, 12.70, false, false, null, null);

        // Mock
        when(ticketService.getTicketById(ticketId)).thenReturn(ticket);

        // Act & Assert
        mockMvc.perform(get("/reservation/tickets/{ticketId}", ticketId))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(12.70))
                .andDo(print());
    }


    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}