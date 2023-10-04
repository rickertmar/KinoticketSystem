package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.*;
import com.dhbw.kinoticket.repository.LocationAddressRepository;
import com.dhbw.kinoticket.repository.UserRepository;
import com.dhbw.kinoticket.request.CreateLocationRequest;
import com.dhbw.kinoticket.request.UpdateUserRequest;
import com.dhbw.kinoticket.response.LocationResponse;
import com.dhbw.kinoticket.response.UserResponse;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = {UserServiceTest.class})
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private LocationAddressRepository locationAddressRepository;
    @InjectMocks
    private UserService userService;

    User user1;
    User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, locationAddressRepository);

        user1 = new User(1L, "John", "Doe", "john@doe.com", "password", Role.USER, null, null, null);
        user2 = new User(2L, "Max", "Mustermann", "max@mustermann.com", "password123", Role.USER, null, null, null);
    }


    @Test
    @Order(1)
    void test_GetAllUsers_WhenAllUsersFound_ThenSuccess() {
        // Arrange
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        // Mock
        when(userRepository.findAll()).thenReturn(userList);

        // Act and Assert
        assertEquals(2, userService.getAllUsers().size());
    }

    @Test
    @Order(2)
    void test_GetAllCinemas_WhenNoCinemasFound_ThenListIsEmpty() {
        // Arrange
        List<User> userList = new ArrayList<>();

        // Mock
        when(userRepository.findAll()).thenReturn(userList);

        // Act and Assert
        assertEquals(0, userService.getAllUsers().size());
    }

    @Test
    @Order(3)
    public void test_GetUserById_WhenIdIsValid_ThenReturnsCorrectUser() {
        // Arrange
        Long id = 1L;

        // Mock
        when(userRepository.findById(id)).thenReturn(Optional.of(user1));

        // Act
        User actualUser = userService.getUserById(id);

        // Assert
        verify(userRepository, times(1)).findById(id);
        assertThat(actualUser).isEqualTo(user1);
    }

    @Test
    @Order(4)
    public void test_GetUserById_WhenIdDoesNotExist_ThenThrowsIllegalArgumentException() {
        // Arrange
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found with ID: " + id);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @Order(5)
    public void test_GetUserByEmail_WhenEmailIsValid_ThenReturnsCorrectUser() {
        // Arrange
        String email = "joe@doe.com";

        // Mock
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user1));

        // Act
        User actualUser = userService.getUserByEmail(email);

        // Assert
        verify(userRepository, times(1)).findByEmail(email);
        assertThat(actualUser).isEqualTo(user1);
    }

    @Test
    @Order(6)
    public void test_GetUserByEmail_WhenEmailDoesNotExist_ThenThrowsIllegalArgumentException() {
        // Arrange
        String email = "joe@doe.com";

        // Mock
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found with E-Mail: " + email);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @Order(7)
    public void test_UserResponseBuilder() {
        // Arrange
        User testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        // Act
        UserResponse userResponse = userService.userResponseBuilder(testUser);

        // Assert
        assertEquals("John", userResponse.getFirstName());
        assertEquals("Doe", userResponse.getLastName());
        assertEquals("john.doe@example.com", userResponse.getEmail());
    }

    @Test
    @Order(8)
    public void test_UpdateUser_WhenValidRequest_ThenUserUpdated() {
        // Arrange
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("NewFirstName", "NewLastName");
        User existingUser = User.builder()
                .firstName("OldFirstName")
                .lastName("OldLastName")
                .email("old.email@example.com")
                .build();

        // Mock
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        UserResponse result = userService.updateUser(updateUserRequest, existingUser);

        // Assert
        verify(userRepository, times(1)).save(existingUser);
        assertEquals(result.getFirstName(), updateUserRequest.getFirstName());
        assertEquals(result.getLastName(), updateUserRequest.getLastName());
        assertEquals(result.getEmail(), existingUser.getEmail());
    }

    @Test
    @Order(9)
    public void test_LocationResponseBuilder() {
        // Arrange
        LocationAddress locationAddress = LocationAddress.builder()
                .street("123 Main St")
                .city("Cityville")
                .country("Countryland")
                .postcode("12345")
                .build();

        // Act
        LocationResponse locationResponse = userService.locationResponseBuilder(locationAddress);

        // Assert
        assertEquals("123 Main St", locationResponse.getStreet());
        assertEquals("Cityville", locationResponse.getCity());
        assertEquals("Countryland", locationResponse.getCountry());
        assertEquals("12345", locationResponse.getPostcode());
    }

    @Test
    @Order(10)
    public void test_LocationAddressBuilder() {
        // Arrange
        CreateLocationRequest createLocationRequest = CreateLocationRequest.builder()
                .street("123 Main St")
                .city("Cityville")
                .country("Countryland")
                .postcode("12345")
                .build();

        // Act
        LocationAddress locationAddress = userService.locationAddressBuilder(createLocationRequest);

        // Assert
        assertEquals("123 Main St", locationAddress.getStreet());
        assertEquals("Cityville", locationAddress.getCity());
        assertEquals("Countryland", locationAddress.getCountry());
        assertEquals("12345", locationAddress.getPostcode());
    }

    @Test
    @Order(11)
    public void test_SaveShippingLocation_WhenValidArguments_ThenSaveShippingLocation() {
        // Arrange
        CreateLocationRequest locationRequest = CreateLocationRequest.builder()
                .street("123 Main St")
                .city("Cityville")
                .country("Countryland")
                .postcode("12345")
                .build();

        // Mock
        when(userRepository.save(user1)).thenReturn(user1);

        // Act
        LocationResponse locationResponse = userService.saveShippingLocation(locationRequest, user1);

        // Assert
        assertEquals("123 Main St", user1.getShippingLocation().getStreet());
        assertEquals("Cityville", user1.getShippingLocation().getCity());
        assertEquals("Countryland", user1.getShippingLocation().getCountry());
        assertEquals("12345", user1.getShippingLocation().getPostcode());
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    @Order(12)
    public void test_UpdateShippingLocation_WhenValidRequest_ThenShippingLocationUpdated() {
        // Arrange
        CreateLocationRequest createLocationRequest = new CreateLocationRequest("123 Updated St", "Updated City", "Updated Country", "54321");
        LocationAddress existingShippingLocation = LocationAddress.builder()
                .street("Old Street")
                .city("Old City")
                .country("Old Country")
                .postcode("12345")
                .build();
        user1.setShippingLocation(existingShippingLocation);

        // Mock
        when(locationAddressRepository.save(existingShippingLocation)).thenReturn(existingShippingLocation);
        when(userRepository.save(user1)).thenReturn(user1);

        // Act
        LocationResponse locationResponse = userService.updateShippingLocation(createLocationRequest, user1);

        // Assert
        assertEquals("123 Updated St", user1.getShippingLocation().getStreet());
        assertEquals("Updated City", user1.getShippingLocation().getCity());
        assertEquals("Updated Country", user1.getShippingLocation().getCountry());
        assertEquals("54321", user1.getShippingLocation().getPostcode());

        verify(locationAddressRepository, times(1)).save(existingShippingLocation);
        verify(userRepository, times(1)).save(user1);

        assertEquals("123 Updated St", locationResponse.getStreet());
        assertEquals("Updated City", locationResponse.getCity());
        assertEquals("Updated Country", locationResponse.getCountry());
        assertEquals("54321", locationResponse.getPostcode());
    }

    @Test
    @Order(13)
    public void test_DeleteShippingLocation_WhenBillingLocationExists_ThenDeleteIsCalled() {
        // Arrange
        LocationAddress existingShippingLocation = LocationAddress.builder()
                .street("Old Street")
                .city("Old City")
                .country("Old Country")
                .postcode("12345")
                .build();
        user1.setShippingLocation(existingShippingLocation);

        // Act
        userService.deleteShippingLocation(user1);

        // Assert
        verify(locationAddressRepository, times(1)).delete(existingShippingLocation);
        assertNull(user1.getShippingLocation());
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    @Order(14)
    public void test_SaveBillingLocation_WhenValidArguments_ThenSaveBillingLocation() {
        // Arrange
        CreateLocationRequest locationRequest = CreateLocationRequest.builder()
                .street("123 Main St")
                .city("Cityville")
                .country("Countryland")
                .postcode("12345")
                .build();

        // Mock
        when(userRepository.save(user1)).thenReturn(user1);

        // Act
        LocationResponse locationResponse = userService.saveBillingLocation(locationRequest, user1);

        // Assert
        assertEquals("123 Main St", user1.getBillingLocation().getStreet());
        assertEquals("Cityville", user1.getBillingLocation().getCity());
        assertEquals("Countryland", user1.getBillingLocation().getCountry());
        assertEquals("12345", user1.getBillingLocation().getPostcode());
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    @Order(15)
    public void test_UpdateBillingLocation_WhenValidRequest_ThenBillingLocationUpdated() {
        // Arrange
        CreateLocationRequest createLocationRequest = new CreateLocationRequest("123 Updated St", "Updated City", "Updated Country", "54321");
        LocationAddress existingBillingLocation = LocationAddress.builder()
                .street("Old Street")
                .city("Old City")
                .country("Old Country")
                .postcode("12345")
                .build();
        user1.setBillingLocation(existingBillingLocation);

        // Mock
        when(locationAddressRepository.save(existingBillingLocation)).thenReturn(existingBillingLocation);
        when(userRepository.save(user1)).thenReturn(user1);

        // Act
        LocationResponse locationResponse = userService.updateBillingLocation(createLocationRequest, user1);

        // Assert
        assertEquals("123 Updated St", user1.getBillingLocation().getStreet());
        assertEquals("Updated City", user1.getBillingLocation().getCity());
        assertEquals("Updated Country", user1.getBillingLocation().getCountry());
        assertEquals("54321", user1.getBillingLocation().getPostcode());

        verify(locationAddressRepository, times(1)).save(existingBillingLocation);
        verify(userRepository, times(1)).save(user1);

        assertEquals("123 Updated St", locationResponse.getStreet());
        assertEquals("Updated City", locationResponse.getCity());
        assertEquals("Updated Country", locationResponse.getCountry());
        assertEquals("54321", locationResponse.getPostcode());
    }

    @Test
    @Order(16)
    public void test_DeleteBillingLocation_WhenBillingLocationExists_ThenDeleteIsCalled() {
        // Arrange
        LocationAddress existingBillingLocation = LocationAddress.builder()
                .street("Old Street")
                .city("Old City")
                .country("Old Country")
                .postcode("12345")
                .build();
        user1.setBillingLocation(existingBillingLocation);

        // Act
        userService.deleteBillingLocation(user1);

        // Assert
        verify(locationAddressRepository, times(1)).delete(existingBillingLocation);
        assertNull(user1.getBillingLocation());
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    @Order(17)
    public void test_DeleteUser_WhenUserExists_ThenDeleteIsCalled() {
        // Act
        userService.deleteUser(user1);

        // Assert
        verify(userRepository, times(1)).delete(user1);
    }
}