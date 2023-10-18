package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.User;
import com.dhbw.kinoticket.request.CreateLocationRequest;
import com.dhbw.kinoticket.request.UpdateUserRequest;
import com.dhbw.kinoticket.response.UserResponse;
import com.dhbw.kinoticket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return new ResponseEntity<>("Null value returned.", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(user, HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email) {
        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return new ResponseEntity<>("Null value returned.", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(user, HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            if (users == null) {
                return new ResponseEntity<>("Null value returned.", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(users, HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('admin:delete')")
    @DeleteMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?>deleteUserById(@PathVariable("id") Long id) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
            }
            userService.deleteUser(user);
            return new ResponseEntity<>("User deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete User.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('admin:delete')")
    @DeleteMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUserByEmail(@PathVariable("email") String email) {
        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
            }
            userService.deleteUser(user);
            return new ResponseEntity<>("User deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete User.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> getSelf(HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        return ResponseEntity.ok(userService.userResponseBuilder(userService.getUserByEmail(principal.getName())));
    }

    @DeleteMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteSelf(HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            userService.deleteUser(userService.getUserByEmail(principal.getName()));
            return new ResponseEntity<>("User deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete User.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateSelf(@RequestBody UpdateUserRequest updateUserRequest, HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            return new ResponseEntity<>(userService.updateUser(updateUserRequest, userService.getUserByEmail(principal.getName())), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update User", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/shipping", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addShippingAddress(@RequestBody CreateLocationRequest createLocationRequest, HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            return new ResponseEntity<>(userService.saveShippingLocation(createLocationRequest, userService.getUserByEmail(principal.getName())), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save Location.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/shipping", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateShippingAddress(@RequestBody CreateLocationRequest createLocationRequest, HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            return new ResponseEntity<>(userService.updateShippingLocation(createLocationRequest, userService.getUserByEmail(principal.getName())), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save Location.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/shipping", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteShippingAddress(HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            userService.deleteShippingLocation(userService.getUserByEmail(principal.getName()));
            return new ResponseEntity<>("Successfully deleted Location", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete Location.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/billing", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addBillingAddress(@RequestBody CreateLocationRequest createLocationRequest, HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            return new ResponseEntity<>(userService.saveBillingLocation(createLocationRequest, userService.getUserByEmail(principal.getName())), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save Location.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/billing", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBillingAddress(@RequestBody CreateLocationRequest createLocationRequest, HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        try {
            return new ResponseEntity<>(userService.updateBillingLocation(createLocationRequest, userService.getUserByEmail(principal.getName())), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save Location.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/billing", produces = MediaType.APPLICATION_JSON_VALUE)
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
