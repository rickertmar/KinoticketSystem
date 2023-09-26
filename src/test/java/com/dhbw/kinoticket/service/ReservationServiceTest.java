package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.*;
import com.dhbw.kinoticket.repository.ReservationRepository;
import com.dhbw.kinoticket.repository.ShowingRepository;
import com.dhbw.kinoticket.request.CreateReservationRequest;
import com.dhbw.kinoticket.request.UpdateSeatStatusRequest;
import com.dhbw.kinoticket.response.MovieResponse;
import com.dhbw.kinoticket.response.ReservationResponse;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {ReservationServiceTest.class})
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ShowingRepository showingRepository;

    @Mock
    private ShowingService showingService;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(reservationRepository, showingRepository, showingService, ticketService);
    }

    @Test
    @Order(1)
    public void test_GetWorkerReservationById_WhenIdExists_ThenReturnReservation() {
        // Arrange
        Long id = 1L;
        Reservation reservation1 = new Reservation();
        reservation1.setId(id);
        reservation1.setUser(User.builder().id(1L).email("test@test.dev").build());
        reservation1.setShowing(Showing.builder().id(1L).time(LocalDateTime.now()).movie(Movie.builder().title("Test movie").build()).build());

        // Mock
        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation1));

        // Act
        var result = reservationService.getWorkerReservationById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    @Order(2)
    public void test_GetWorkerReservationById_WhenIdDoesNotExist_ThenThrowException() {
        // Arrange
        Long id = 1L;
        when(reservationRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.getWorkerReservationById(id);
        });
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
        reservation.setUser(User.builder().id(1L).email("test@test.dev").build());
        reservation.setShowing(Showing.builder().id(1L).time(LocalDateTime.now()).movie(Movie.builder().title("Test movie").build()).build());

        // Act
        var result = reservationService.convertToWorkerResponse(reservation);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100.0, result.getTotal());
        assertTrue(result.isPaid());
    }

    @Test
    @Order(5)
    public void test_CreateReservation_WhenValidRequestAndUser_ThenReturnReservationResponse() {
        // Arrange
        CreateReservationRequest request = new CreateReservationRequest();
        request.setSelectedSeatIdList(List.of(1L, 2L));
        request.setChildDiscounts(1);
        request.setStudentDiscounts(1);
        request.setNoDiscounts(0);
        request.setShowingId(1L);

        User user = new User();

        Reservation reservation = new Reservation();
        reservation.setTotal(5.0);
        reservation.setTickets(new ArrayList<>());

        Seat seat1 = new Seat();
        seat1.setId(1L);
        Seat seat2 = new Seat();
        seat2.setId(2L);
        Set<Seat> seatsSet = new HashSet<>();
        seatsSet.add(seat1);
        seatsSet.add(seat2);

        Movie movie = new Movie(1L, "Movie title", FSK.FSK16, "Movie description", 2023, "Horror", "Director", 2, "1h 20min", "USA", "src", "Actors", null);

        Showing showing = new Showing();
        showing.setSeatPrice(5.0);
        showing.setSeats(seatsSet);
        showing.setMovie(movie);

        List<Ticket> tickets = new ArrayList<>();
        Ticket ticket1 = new Ticket();
        ticket1.setId(1L);
        ticket1.setDiscount(Discount.STUDENT);
        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);
        ticket2.setDiscount(Discount.CHILD);
        tickets.add(ticket1);
        tickets.add(ticket2);

        // Mock
        when(showingService.getShowingById(request.getShowingId())).thenReturn(showing);
        when(ticketService.createTicket(any(Discount.class), any(Reservation.class), any(Seat.class))).thenReturn(ticket1, ticket2);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Act
        ReservationResponse result = reservationService.createReservation(request, user);

        // Assert
        assertNotNull(result);
        assertEquals(showing.getMovie().getTitle(), result.getMovie().getTitle());
        assertEquals(tickets, result.getTickets());
        assertEquals(reservation.getTotal(), result.getTotal());
    }

    @Test
    @Order(6)
    public void test_CreateReservation_WhenNoSelectedSeats_ThenReturnReservationResponseWithEmptyTickets() {
        // Arrange
        CreateReservationRequest request = new CreateReservationRequest();
        request.setShowingId(1L);
        request.setSelectedSeatIdList(Collections.emptyList());
        // Set other properties of the request as needed

        User user = new User();

        Seat seat1 = new Seat();
        seat1.setId(1L);
        Seat seat2 = new Seat();
        seat2.setId(2L);
        Set<Seat> seatsSet = new HashSet<>();
        seatsSet.add(seat1);
        seatsSet.add(seat2);

        Movie movie = new Movie(1L, "Movie title", FSK.FSK16, "Movie description", 2023, "Horror", "Director", 2, "1h 20min", "USA", "src", "Actors", null);

        Showing showing = new Showing();
        showing.setSeatPrice(5.0);
        showing.setSeats(seatsSet);
        showing.setMovie(movie);

        when(showingService.getShowingById(request.getShowingId())).thenReturn(showing);

        // Act
        ReservationResponse result = reservationService.createReservation(request, user);

        // Assert
        assertNotNull(result);
        assertEquals(showing.getMovie().getTitle(), result.getMovie().getTitle());
        assertEquals(showing.getTime(), result.getTime());
        assertTrue(result.getTickets().isEmpty());
        assertEquals(0.0, result.getTotal());
    }

    @Test
    @Order(7)
    public void test_CreateReservation_WhenInvalidShowingId_ThenThrowException() {
        // Arrange
        CreateReservationRequest request = new CreateReservationRequest();
        request.setShowingId(1L);

        User user = new User();

        // Mock
        when(showingService.getShowingById(request.getShowingId())).thenThrow(new IllegalArgumentException("Showing not found with ID: " + request.getShowingId()));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(request, user);
        });
    }

    @Test
    @Order(8)
    public void test_CreateReservation_WhenNoUser_ThenThrowException() {
        // Arrange
        CreateReservationRequest request = new CreateReservationRequest();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            reservationService.createReservation(request, null);
        });
    }

    @Test
    @Order(9)
    public void test_CreateReservation_WhenSeatNotFound_ThenThrowException() {
        // Arrange
        CreateReservationRequest request = new CreateReservationRequest();
        request.setSelectedSeatIdList(List.of(1L, 2L));
        request.setChildDiscounts(1);
        request.setStudentDiscounts(1);
        request.setNoDiscounts(0);
        request.setShowingId(1L);

        User user = new User();

        Reservation reservation = new Reservation();
        reservation.setTotal(10.0);

        Seat seat1 = new Seat();
        seat1.setId(1L);

        Set<Seat> seats = new HashSet<>(); // Empty seats set

        Showing showing = new Showing();
        showing.setSeatPrice(5.0);
        showing.setSeats(seats);

        when(showingService.getShowingById(request.getShowingId())).thenReturn(showing);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(request, user);
        });
    }

    @Test
    @Order(10)
    public void test_CalculateTotalPrice_RegularPrice() {
        // Create a mock Showing object
        Showing showing = new Showing();
        showing.setSeatPrice(10.0);

        // Create mock Ticket objects
        List<Ticket> tickets = new ArrayList<>();
        Ticket ticket1 = new Ticket();
        ticket1.setDiscount(Discount.REGULAR);
        tickets.add(ticket1);

        Ticket ticket2 = new Ticket();
        ticket2.setDiscount(Discount.REGULAR);
        tickets.add(ticket2);

        // Call the method under test
        double totalPrice = reservationService.calculateTotalPrice(showing, tickets);

        // Assert the total price
        assertEquals(20.0, totalPrice);
    }

    @Test
    @Order(11)
    public void test_CalculateTotalPrice_StudentDiscount() {
        // Create a mock Showing object
        Showing showing = new Showing();
        showing.setSeatPrice(10.0);

        // Create mock Ticket objects
        List<Ticket> tickets = new ArrayList<>();
        Ticket ticket1 = new Ticket();
        ticket1.setDiscount(Discount.STUDENT);
        tickets.add(ticket1);

        Ticket ticket2 = new Ticket();
        ticket2.setDiscount(Discount.STUDENT);
        tickets.add(ticket2);

        // Call the method under test
        double totalPrice = reservationService.calculateTotalPrice(showing, tickets);

        // Assert the total price
        assertEquals(16.0, totalPrice);
    }

    @Test
    @Order(12)
    public void test_CalculateTotalPrice_ChildDiscount() {
        // Create a mock Showing object
        Showing showing = new Showing();
        showing.setSeatPrice(10.0);

        // Create mock Ticket objects
        List<Ticket> tickets = new ArrayList<>();
        Ticket ticket1 = new Ticket();
        ticket1.setDiscount(Discount.CHILD);
        tickets.add(ticket1);

        Ticket ticket2 = new Ticket();
        ticket2.setDiscount(Discount.CHILD);
        tickets.add(ticket2);

        // Call the method under test
        double totalPrice = reservationService.calculateTotalPrice(showing, tickets);

        // Assert the total price
        assertEquals(14.0, totalPrice);
    }

    @Test
    @Order(13)
    public void test_ConvertToMovieDTO() {
        // Create a mock Movie object
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Test Movie");
        movie.setFsk(FSK.FSK16);
        movie.setDescription("Test Description");
        movie.setReleaseYear(2021);
        movie.setGenres("Action, Thriller");
        movie.setDirector("John Doe");
        movie.setRunningWeek(2);
        movie.setRuntime("2h 30min");
        movie.setReleaseCountry("USA");
        movie.setImageSrc("image.jpg");
        movie.setActors("Actor 1, Actor 2");

        // Call the method under test
        MovieResponse result = reservationService.convertToMovieDTO(movie);

        // Assert the MovieResponse object
        assertNotNull(result);
        assertEquals(movie.getId(), result.getId());
        assertEquals(movie.getTitle(), result.getTitle());
        assertEquals(movie.getFsk(), result.getFsk());
        assertEquals(movie.getDescription(), result.getDescription());
        assertEquals(movie.getReleaseYear(), result.getReleaseYear());
        assertEquals(movie.getGenres(), result.getGenres());
        assertEquals(movie.getDirector(), result.getDirector());
        assertEquals(movie.getRunningWeek(), result.getRunningWeek());
        assertEquals(movie.getRuntime(), result.getRuntime());
        assertEquals(movie.getReleaseCountry(), result.getReleaseCountry());
        assertEquals(movie.getImageSrc(), result.getImageSrc());
        assertEquals(movie.getActors(), result.getActors());
    }

}