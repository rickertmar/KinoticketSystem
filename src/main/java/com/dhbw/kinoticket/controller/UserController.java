package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.User;
import com.dhbw.kinoticket.request.CreateLocationRequest;
import com.dhbw.kinoticket.request.UpdateUserRequest;
import com.dhbw.kinoticket.response.UserResponse;
import com.dhbw.kinoticket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        try {
            User user = userService.getUserById(id);
            return new ResponseEntity<>(user, HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping(value = "/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable("email") String email) {
        try {
            User user = userService.getUserByEmail(email);
            return new ResponseEntity<>(user, HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping(value = "/all")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PreAuthorize("hasAuthority('admin:delete')")
    @DeleteMapping(value = "/id/{id}")
    public ResponseEntity<?>deleteUserById(@PathVariable("id") Long id) {
        try {
            userService.deleteUser(userService.getUserById(id));
            return new ResponseEntity<>("User deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete Cinema.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PreAuthorize("hasAuthority('admin:delete')")
    @DeleteMapping(value = "/email/{email}")
    public ResponseEntity<?> deleteUserByEmail(@PathVariable("email") String email) {
        try {
            userService.deleteUser(userService.getUserByEmail(email));
            return new ResponseEntity<>("User deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete User.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "")
    public ResponseEntity<UserResponse> getSelf(HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        return ResponseEntity.ok(userService.userResponseBuilder(userService.getUserByEmail(principal.getName())));
    }
    @DeleteMapping(value = "")
    public ResponseEntity<?> deleteSelf(HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            userService.deleteUser(userService.getUserByEmail(principal.getName()));
            return new ResponseEntity<>("User deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete User.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping(value = "")
    public ResponseEntity<?> updateSelf(@RequestBody UpdateUserRequest updateUserRequest, HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            return new ResponseEntity<>(userService.updateUser(updateUserRequest, userService.getUserByEmail(principal.getName())), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update User", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/shipping")
    public ResponseEntity<?> addShippingAddress(@RequestBody CreateLocationRequest createLocationRequest, HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            return new ResponseEntity<>(userService.saveShippingLocation(createLocationRequest, userService.getUserByEmail(principal.getName())), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save Location.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping(value = "/shipping")
    public ResponseEntity<?> updateShippingAddress(@RequestBody CreateLocationRequest createLocationRequest, HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            return new ResponseEntity<>(userService.updateShippingLocation(createLocationRequest, userService.getUserByEmail(principal.getName())), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save Location.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping(value = "/shipping")
    public ResponseEntity<?> deleteShippingAddress(HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            userService.deleteShippingLocation(userService.getUserByEmail(principal.getName()));
            return new ResponseEntity<>("Successfully deleted Location", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete Location.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping(value = "/billing")
    public ResponseEntity<?> addBillingAddress(@RequestBody CreateLocationRequest createLocationRequest, HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            return new ResponseEntity<>(userService.saveBillingLocation(createLocationRequest, userService.getUserByEmail(principal.getName())), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save Location.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping(value = "/billing")
    public ResponseEntity<?> updateBillingAddress(@RequestBody CreateLocationRequest createLocationRequest, HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            return new ResponseEntity<>(userService.updateBillingLocation(createLocationRequest, userService.getUserByEmail(principal.getName())), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save Location.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping(value = "/billing")
    public ResponseEntity<?> deleteBillingAddress(HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            userService.deleteBillingLocation(userService.getUserByEmail(principal.getName()));
            return new ResponseEntity<>("Successfully deleted Location", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete Location.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}