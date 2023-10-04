package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.Role;
import com.dhbw.kinoticket.entity.User;
import com.dhbw.kinoticket.repository.LocationAddressRepository;
import com.dhbw.kinoticket.request.CreateLocationRequest;
import com.dhbw.kinoticket.request.UpdateUserRequest;
import com.dhbw.kinoticket.response.LocationResponse;
import com.dhbw.kinoticket.response.UserResponse;
import com.dhbw.kinoticket.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan(basePackages = "com.dhbw.kinoticket")
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(classes = {UserControllerTest.class})
@AutoConfigureJsonTesters
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private LocationAddressRepository locationAddressRepository;

    @InjectMocks
    private UserController userController;

    User user1;
    User user2;
    List<User> userList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        user1 = new User(1L, "John", "Doe", "john@doe.com", "password", Role.USER, null, null, null);
        user2 = new User(2L, "Max", "Mustermann", "max@mustermann.com", "password123", Role.USER, null, null, null);
        userList = Arrays.asList(user1, user2);
    }


    @Test
    @Order(1)
    void test_GetUserById_ShouldReturnUserById() throws Exception {
        // Arrange
        Long id = 1L;

        // Mock
        when(userService.getUserById(id)).thenReturn(user1);

        // Act and Assert
        mockMvc.perform(get("/users/id/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value("john@doe.com"))
                .andDo(print());

        verify(userService, times(1)).getUserById(id);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(2)
    void test_GetUserByEmail_ShouldReturnUserByEmail() throws Exception {
        // Arrange
        String email = "john@doe.com";

        // Mock
        when(userService.getUserByEmail(email)).thenReturn(user1);

        // Act and Assert
        mockMvc.perform(get("/users/email/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value(email))
                .andDo(print());

        verify(userService, times(1)).getUserByEmail(email);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(3)
    public void test_GetAllUsers_ShouldReturnAllUsers() throws Exception {
        // Mock
        when(userService.getAllUsers()).thenReturn(userList);

        // Act and Assert
        mockMvc.perform(get("/users/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[0].email").value("john@doe.com"))
                .andExpect(jsonPath("$[1].email").value("max@mustermann.com"))
                .andDo(print());
        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(4)
    void test_DeleteUserById_Success() throws Exception {
        // Arrange
        Long userId = 1L;

        // Mock
        when(userService.getUserById(userId)).thenReturn(user1);

        // Act and Assert
        mockMvc.perform(delete("/users/id/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted."))
                .andDo(print());

        verify(userService, times(1)).deleteUser(user1);
        verify(userService, times(1)).getUserById(userId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(5)
    void test_DeleteUserById_UserNotFound() throws Exception {
        // Arrange
        Long userId = 2L;

        // Mock
        when(userService.getUserById(userId)).thenReturn(null);

        // Act and Assert
        mockMvc.perform(delete("/users/id/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found."))
                .andDo(print());

        verify(userService, never()).deleteUser(any(User.class));
        verify(userService, times(1)).getUserById(userId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(6)
    void test_DeleteUserByEmail_Success() throws Exception {
        // Arrange
        String userEmail = "john@doe.com";

        // Mock
        when(userService.getUserByEmail(userEmail)).thenReturn(user1);

        // Act and Assert
        mockMvc.perform(delete("/users/email/{email}", userEmail))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted."))
                .andDo(print());

        verify(userService, times(1)).deleteUser(user1);
        verify(userService, times(1)).getUserByEmail(userEmail);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(7)
    void test_DeleteUserByEmail_UserNotFound() throws Exception {
        // Arrange
        String userEmail = "nonexistent@example.com";

        // Mock
        when(userService.getUserByEmail(userEmail)).thenReturn(null);

        // Act and Assert
        mockMvc.perform(delete("/users/email/{email}", userEmail))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found."))
                .andDo(print());

        verify(userService, never()).deleteUser(any(User.class));
        verify(userService, times(1)).getUserByEmail(userEmail);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(8)
    void test_GetSelf_WhenCalled_ThenUserServiceGetUserByEmailIsCalledWithCorrectEmail() throws Exception {
        // Arrange
        Principal principal = mock(Principal.class);
        User user = new User();
        user.setEmail("john@doe.com");

        // Mock
        when(principal.getName()).thenReturn("john@doe.com");
        when(httpServletRequest.getUserPrincipal()).thenReturn(principal);
        when(userService.getUserByEmail("john@doe.com")).thenReturn(user);

        // Act
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andDo(print());

        // Assert
        verify(userService, times(1)).getUserByEmail("john@doe.com");
    }

    @Test
    @Order(9)
    void test_GetSelf_WhenCalled_ThenReturnsResponseEntityWithCorrectUserResponse() throws Exception {
        // Arrange
        Principal principal = mock(Principal.class);
        User user = new User();
        user.setEmail("john@doe.com");
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail("john@doe.com");

        // Mock
        when(principal.getName()).thenReturn("john@doe.com");
        when(httpServletRequest.getUserPrincipal()).thenReturn(principal);
        when(userService.getUserByEmail("john@doe.com")).thenReturn(user);
        when(userService.userResponseBuilder(user)).thenReturn(userResponse);

        // Act and Assert
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@doe.com"))
                .andDo(print());
    }

    @Test
    @Order(10)
    void test_DeleteSelf_Success() throws Exception {
        // Mock the user principal
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");

        // Mock the userService methods
        User user = new User();
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        doNothing().when(userService).deleteUser(any(User.class));

        // Perform the DELETE request
        mockMvc.perform(delete("/users")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted."))
                .andDo(print());
    }

    @Test
    @Order(11)
    void test_DeleteSelf_Failure() throws Exception {
        // Mock the user principal
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");

        // Mock the userService methods to throw an exception
        when(userService.getUserByEmail(anyString())).thenThrow(new RuntimeException());

        // Perform the DELETE request
        mockMvc.perform(delete("/users")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to delete User."))
                .andDo(print());
    }

    @Test
    @Order(12)
    void test_UpdateSelf_Success() throws Exception {
        // Mock the principal and update request
        Principal mockPrincipal = () -> "testuser@example.com";
        UpdateUserRequest mockUpdateRequest = new UpdateUserRequest();
        mockUpdateRequest.setFirstName("NewFM");

        UserResponse userResponse = UserResponse.builder().firstName("NewFN").build();
        User user = User.builder().firstName("OldFN").build();

        // Mock the UserService behavior for successful update
        when(userService.getUserByEmail(mockPrincipal.getName())).thenReturn(user);
        when(userService.updateUser(mockUpdateRequest, user)).thenReturn(userResponse);

        // Perform the PUT request
        mockMvc.perform(put("/users")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mockUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Verify that the UserService methods were called
        verify(userService, times(1)).getUserByEmail(mockPrincipal.getName());
        verify(userService, times(1)).updateUser(mockUpdateRequest, user);
    }

    @Test
    @Order(13)
    void test_UpdateSelf_Failure() throws Exception {
        // Mock the principal and update request
        Principal mockPrincipal = () -> "testuser@example.com";
        UpdateUserRequest mockUpdateRequest = new UpdateUserRequest();
        mockUpdateRequest.setFirstName("NewFM");

        UserResponse userResponse = UserResponse.builder().firstName("NewFN").build();
        User user = User.builder().firstName("OldFN").build();

        // Mock the UserService behavior for successful update
        when(userService.getUserByEmail(mockPrincipal.getName())).thenReturn(user);
        when(userService.updateUser(mockUpdateRequest, user)).thenThrow(new RuntimeException("Update failed"));

        // Perform the PUT request
        mockMvc.perform(put("/users")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mockUpdateRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to update User"))
                .andDo(print());
    }

    @Test
    @Order(14)
    void test_AddShippingAddress_Success() throws Exception {
        // Mock the principal and create location request
        Principal mockPrincipal = () -> "testuser@example.com";
        CreateLocationRequest mockCreateLocationRequest = CreateLocationRequest.builder()
                .street("123 Main St")
                .city("City")
                .country("Country")
                .postcode("12345")
                .build();

        User mockUser = User.builder().email(mockPrincipal.getName()).build();
        LocationResponse mockLocation = LocationResponse.builder()
                .street(mockCreateLocationRequest.getStreet())
                .city(mockCreateLocationRequest.getCity())
                .country(mockCreateLocationRequest.getCountry())
                .postcode(mockCreateLocationRequest.getPostcode())
                .build();

        // Mock the UserService behavior for successful save
        when(userService.getUserByEmail(mockPrincipal.getName())).thenReturn(mockUser);
        when(userService.saveShippingLocation(eq(mockCreateLocationRequest), any(User.class))).thenReturn(mockLocation);

        // Perform the POST request
        mockMvc.perform(post("/users/shipping")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mockCreateLocationRequest)))
                .andExpect(status().isOk())
                .andDo(print());

        // Verify that the UserService methods were called
        verify(userService).getUserByEmail(mockPrincipal.getName());
        verify(userService).saveShippingLocation(eq(mockCreateLocationRequest), any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(15)
    void test_AddShippingAddress_Failure() throws Exception {
        // Mock the principal and create location request
        Principal mockPrincipal = () -> "testuser@example.com";
        CreateLocationRequest mockCreateLocationRequest = CreateLocationRequest.builder()
                .street("123 Main St")
                .city("City")
                .country("Country")
                .postcode("12345")
                .build();

        User mockUser = User.builder().email(mockPrincipal.getName()).build();

        // Mock the UserService behavior for failure
        when(userService.getUserByEmail(mockPrincipal.getName())).thenReturn(mockUser);
        when(userService.saveShippingLocation(mockCreateLocationRequest, mockUser)).thenThrow(new RuntimeException("Failed to add ShippingAddress"));

        // Perform the POST request
        mockMvc.perform(post("/users/shipping")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mockCreateLocationRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to save Location."))
                .andDo(print());

        // Verify that the UserService methods were called
        verify(userService).getUserByEmail(mockPrincipal.getName());
        verify(userService).saveShippingLocation(eq(mockCreateLocationRequest), any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(16)
    void test_AddBillingAddress_Success() throws Exception {
        // Mock the principal and create location request
        Principal mockPrincipal = () -> "testuser@example.com";
        CreateLocationRequest mockCreateLocationRequest = CreateLocationRequest.builder()
                .street("123 Main St")
                .city("City")
                .country("Country")
                .postcode("12345")
                .build();

        User mockUser = User.builder().email(mockPrincipal.getName()).build();
        LocationResponse mockLocation = LocationResponse.builder()
                .street(mockCreateLocationRequest.getStreet())
                .city(mockCreateLocationRequest.getCity())
                .country(mockCreateLocationRequest.getCountry())
                .postcode(mockCreateLocationRequest.getPostcode())
                .build();

        // Mock the UserService behavior for successful save
        when(userService.getUserByEmail(mockPrincipal.getName())).thenReturn(mockUser);
        when(userService.saveBillingLocation(eq(mockCreateLocationRequest), any(User.class))).thenReturn(mockLocation);

        // Perform the POST request
        mockMvc.perform(post("/users/billing")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mockCreateLocationRequest)))
                .andExpect(status().isOk())
                .andDo(print());

        // Verify that the UserService methods were called
        verify(userService).getUserByEmail(mockPrincipal.getName());
        verify(userService).saveBillingLocation(eq(mockCreateLocationRequest), any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(17)
    void test_AddBillingAddress_Failure() throws Exception {
        // Mock the principal and create location request
        Principal mockPrincipal = () -> "testuser@example.com";
        CreateLocationRequest mockCreateLocationRequest = CreateLocationRequest.builder()
                .street("123 Main St")
                .city("City")
                .country("Country")
                .postcode("12345")
                .build();

        User mockUser = User.builder().email(mockPrincipal.getName()).build();

        // Mock the UserService behavior for failure
        when(userService.getUserByEmail(mockPrincipal.getName())).thenReturn(mockUser);
        when(userService.saveBillingLocation(mockCreateLocationRequest, mockUser)).thenThrow(new RuntimeException("Failed to add BillingAddress"));

        // Perform the POST request
        mockMvc.perform(post("/users/billing")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mockCreateLocationRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to save Location."))
                .andDo(print());

        // Verify that the UserService methods were called
        verify(userService).getUserByEmail(mockPrincipal.getName());
        verify(userService).saveBillingLocation(eq(mockCreateLocationRequest), any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(18)
    void test_DeleteShippingAddress_Success() throws Exception {
        // Mock the principal
        Principal mockPrincipal = () -> "testuser@example.com";

        User mockUser = User.builder().email(mockPrincipal.getName()).build();

        // Mock the UserService behavior
        when(userService.getUserByEmail(mockPrincipal.getName())).thenReturn(mockUser);

        // Perform the DELETE request
        mockMvc.perform(delete("/users/shipping")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted Location"))
                .andDo(print());

        // Verify that the UserService methods were called
        verify(userService).getUserByEmail(mockPrincipal.getName());
        verify(userService).deleteShippingLocation(mockUser);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(19)
    void test_DeleteShippingAddress_Failure() throws Exception {
        // Mock the principal
        Principal mockPrincipal = () -> "testuser@example.com";

        User mockUser = User.builder().email(mockPrincipal.getName()).build();

        // Mock the UserService behavior
        when(userService.getUserByEmail(mockPrincipal.getName())).thenReturn(mockUser);
        doThrow(new RuntimeException("Failed to delete ShippingAddress")).when(userService).deleteShippingLocation(mockUser);

        // Perform the DELETE request
        mockMvc.perform(delete("/users/shipping")
                        .principal(mockPrincipal))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to delete Location."));

        // Verify that the UserService methods were called
        verify(userService).getUserByEmail(mockPrincipal.getName());
        verify(userService).deleteShippingLocation(mockUser);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(20)
    void test_DeleteBillingAddress_Success() throws Exception {
        // Mock the principal
        Principal mockPrincipal = () -> "testuser@example.com";

        User mockUser = User.builder().email(mockPrincipal.getName()).build();

        // Mock the UserService behavior
        when(userService.getUserByEmail(mockPrincipal.getName())).thenReturn(mockUser);

        // Perform the DELETE request
        mockMvc.perform(delete("/users/billing")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted Location"))
                .andDo(print());

        // Verify that the UserService methods were called
        verify(userService).getUserByEmail(mockPrincipal.getName());
        verify(userService).deleteBillingLocation(mockUser);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(21)
    void test_DeleteBillingAddress_Failure() throws Exception {
        // Mock the principal
        Principal mockPrincipal = () -> "testuser@example.com";

        User mockUser = User.builder().email(mockPrincipal.getName()).build();

        // Mock the UserService behavior
        when(userService.getUserByEmail(mockPrincipal.getName())).thenReturn(mockUser);
        doThrow(new RuntimeException("Failed to delete ShippingAddress")).when(userService).deleteBillingLocation(mockUser);

        // Perform the DELETE request
        mockMvc.perform(delete("/users/billing")
                        .principal(mockPrincipal))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to delete Location."))
                .andDo(print());

        // Verify that the UserService methods were called
        verify(userService).getUserByEmail(mockPrincipal.getName());
        verify(userService).deleteBillingLocation(mockUser);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(22)
    void test_UpdateShippingAddress_Success() throws Exception {
        // Mock the principal
        Principal mockPrincipal = () -> "testuser@example.com";

        User mockUser = User.builder().email(mockPrincipal.getName()).build();

        // Mock the UserService behavior
        when(userService.getUserByEmail(mockPrincipal.getName())).thenReturn(mockUser);
        when(userService.updateShippingLocation(any(CreateLocationRequest.class), eq(mockUser))).thenReturn(new LocationResponse());

        // Create a sample request body
        CreateLocationRequest request = CreateLocationRequest.builder()
                .street("123 Main St")
                .city("City")
                .country("Country")
                .postcode("12345")
                .build();

        // Perform the PUT request
        mockMvc.perform(put("/users/shipping")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request))
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // Verify that the UserService methods were called
        verify(userService).getUserByEmail(mockPrincipal.getName());
        verify(userService).updateShippingLocation(any(CreateLocationRequest.class), eq(mockUser));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(23)
    void test_UpdateShippingAddress_Failure() throws Exception {
        // Mock the principal
        Principal mockPrincipal = () -> "testuser@example.com";

        User mockUser = User.builder().email(mockPrincipal.getName()).build();

        // Mock the UserService behavior
        when(userService.getUserByEmail(mockPrincipal.getName())).thenReturn(mockUser);
        when(userService.updateShippingLocation(any(CreateLocationRequest.class), eq(mockUser))).thenThrow(new RuntimeException("Failed to update location"));

        // Create a sample request body
        CreateLocationRequest request = CreateLocationRequest.builder()
                .street("123 Main St")
                .city("City")
                .country("Country")
                .postcode("12345")
                .build();

        // Perform the PUT request
        mockMvc.perform(put("/users/shipping")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request))
                        .principal(mockPrincipal))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // Verify that the UserService methods were called
        verify(userService).getUserByEmail(mockPrincipal.getName());
        verify(userService).updateShippingLocation(any(CreateLocationRequest.class), eq(mockUser));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(24)
    void test_UpdateBillingAddress_Success() throws Exception {
        // Mock the principal
        Principal mockPrincipal = () -> "testuser@example.com";

        User mockUser = User.builder().email(mockPrincipal.getName()).build();

        // Mock the UserService behavior
        when(userService.getUserByEmail(mockPrincipal.getName())).thenReturn(mockUser);
        when(userService.updateBillingLocation(any(CreateLocationRequest.class), eq(mockUser))).thenReturn(new LocationResponse());

        // Create a sample request body
        CreateLocationRequest request = CreateLocationRequest.builder()
                .street("123 Main St")
                .city("City")
                .country("Country")
                .postcode("12345")
                .build();

        // Perform the PUT request
        mockMvc.perform(put("/users/billing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request))
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // Verify that the UserService methods were called
        verify(userService).getUserByEmail(mockPrincipal.getName());
        verify(userService).updateBillingLocation(any(CreateLocationRequest.class), eq(mockUser));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @Order(25)
    void test_UpdateBillingAddress_Failure() throws Exception {
        // Mock the principal
        Principal mockPrincipal = () -> "testuser@example.com";

        User mockUser = User.builder().email(mockPrincipal.getName()).build();

        // Mock the UserService behavior
        when(userService.getUserByEmail(mockPrincipal.getName())).thenReturn(mockUser);
        when(userService.updateBillingLocation(any(CreateLocationRequest.class), eq(mockUser))).thenThrow(new RuntimeException("Failed to update location"));

        // Create a sample request body
        CreateLocationRequest request = CreateLocationRequest.builder()
                .street("123 Main St")
                .city("City")
                .country("Country")
                .postcode("12345")
                .build();

        // Perform the PUT request
        mockMvc.perform(put("/users/billing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request))
                        .principal(mockPrincipal))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // Verify that the UserService methods were called
        verify(userService).getUserByEmail(mockPrincipal.getName());
        verify(userService).updateBillingLocation(any(CreateLocationRequest.class), eq(mockUser));
        verifyNoMoreInteractions(userService);
    }


    // Utility method to convert an object to JSON string
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

/*
{
    "id": 1,
    "firstName": "Adi",
    "lastName": "Admin",
    "email": "ADMIN",
    "password": "",
    "role": "ADMIN",
    "tokens": [
        {
            "id": 1,
            "token": "",
            "tokenType": "BEARER",
            "revoked": true,
            "expired": true
        }
    ],
    "shippingLocation": null,
    "billingLocation": null,
    "enabled": true,
    "authorities": [
        {
            "authority": "worker:create"
        },
        {
            "authority": "worker:read"
        },
        {
            "authority": "admin:create"
        },
        {
            "authority": "admin:delete"
        },
        {
            "authority": "worker:update"
        },
        {
            "authority": "worker:delete"
        },
        {
            "authority": "admin:update"
        },
        {
            "authority": "admin:read"
        },
        {
            "authority": "ROLE_ADMIN"
        }
    ],
    "username": "ADMIN",
    "accountNonExpired": true,
    "credentialsNonExpired": true,
    "accountNonLocked": true
}
 */
