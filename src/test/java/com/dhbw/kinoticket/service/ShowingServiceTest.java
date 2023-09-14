package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Showing;
import com.dhbw.kinoticket.repository.ShowingRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {ShowingServiceTest.class})
public class ShowingServiceTest {

    @Mock
    private ShowingRepository showingRepository;

    @InjectMocks
    private ShowingService showingService;

    private Showing showing1;
    private Showing showing2;
    private List<Showing> allShowings;

    @BeforeEach
    void setUp() {
        showing1 = new Showing(
                1L,
                null,
                null,
                "3D",
                null,
                null
        );

        showing2 = new Showing(
                2L,
                null,
                null,
                "2D",
                null,
                null
        );

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
}