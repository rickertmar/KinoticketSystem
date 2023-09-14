package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.Showing;
import com.dhbw.kinoticket.service.ShowingService;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan(basePackages = "com.dhbw.kinoticket")
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(classes = {ShowingControllerTest.class})
class ShowingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    private ShowingService showingService;

    @InjectMocks
    private ShowingController showingController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(showingController).build();
    }


    @Test
    @Order(1)
    void test_GetAllShowings_ShouldReturnAllShowings() throws Exception {
        // Arrange
        List<Showing> showings = new ArrayList<>();
        showings.add(new Showing(1L, null, null, "3D", null, null));
        showings.add(new Showing(2L, null, null, "2D", null, null));

        // Mock
        when(showingService.getAllShowings()).thenReturn(showings);

        // Act and Assert
        mockMvc.perform(get("/showings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(showings.size()))
                .andDo(print());
    }

    @Test
    @Order(2)
    void test_GetShowingById_ShouldReturnShowingById() throws Exception {
        // Arrange
        Long showingId = 1L;
        Showing showing = new Showing(showingId, null, null, "3D", null, null);

        // Mock
        when(showingService.getShowingById(showingId)).thenReturn(showing);

        // Act and Assert
        mockMvc.perform(get("/showings/{id}", showingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(showingId))
                .andDo(print());
    }
}