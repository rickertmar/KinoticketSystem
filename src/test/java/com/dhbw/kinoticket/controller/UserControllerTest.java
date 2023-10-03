package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.Role;
import com.dhbw.kinoticket.entity.User;
import com.dhbw.kinoticket.repository.LocationAddressRepository;
import com.dhbw.kinoticket.service.UserService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
}
