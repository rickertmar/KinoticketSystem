package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.entity.User;
import com.dhbw.kinoticket.repository.LocationAddressRepository;
import com.dhbw.kinoticket.repository.UserRepository;
import com.dhbw.kinoticket.request.CreateLocationRequest;
import com.dhbw.kinoticket.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final UserRepository userRepository;
    private final LocationAddressRepository locationAddressRepository;
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping(value = "/id/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        return userRepository.findById(id).orElse(null);
    }
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping(value = "/email/{email}")
    public User getUserByEmail(@PathVariable("email") String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping(value = "/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @PreAuthorize("hasAuthority('admin:delete')")
    @DeleteMapping(value = "/id/{id}")
    public void deleteUserById(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
    }
    @PreAuthorize("hasAuthority('admin:delete')")
    @DeleteMapping(value = "/email/{email}")
    public void deleteUserByEmail(@PathVariable("email") String email) {
        userRepository.deleteById(userRepository.findByEmail(email).orElseThrow().getId());
    }

    @GetMapping(value = "")
    public ResponseEntity<UserResponse> getSelf(HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        var userResponse = UserResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .billing(user.getBillingLocation())
                .shipping(user.getShippingLocation())
                .build();
        return ResponseEntity.ok(userResponse);
    }
    @PutMapping(value = "")
    public User updateSelf(HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        return userRepository.save(userRepository.findByEmail(principal.getName()).orElseThrow());
    }
    @DeleteMapping(value = "")
    public void deleteSelf(HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        userRepository.deleteById(userRepository.findByEmail(principal.getName()).orElseThrow().getId());
    }
    @PostMapping(value = "/shipping")
    public LocationAddress addShippingAddress(@RequestBody CreateLocationRequest createLocationRequest, HttpServletRequest httpServletRequest) {
        var locationAddress = LocationAddress
                .builder()
                .street(createLocationRequest.getStreet())
                .city(createLocationRequest.getCity())
                .country(createLocationRequest.getCountry())
                .postalcode(createLocationRequest.getPostalCode())
                .build();
        var location = locationAddressRepository.save(locationAddress);
        User user = userRepository.findByEmail(httpServletRequest.getUserPrincipal().getName()).orElseThrow();
        user.setShippingLocation(location);
        return userRepository.save(user).getShippingLocation();
    }
    @PutMapping(value = "/shipping")
    public LocationAddress updateShippingAddress(@RequestBody LocationAddress locationAddress, HttpServletRequest httpServletRequest) {
        User user = userRepository.findByEmail(httpServletRequest.getUserPrincipal().getName()).orElseThrow();
        user.setShippingLocation(locationAddress);
        return userRepository.save(user).getShippingLocation();
    }
    @DeleteMapping(value = "/shipping")
    public void deleteShippingAddress(HttpServletRequest httpServletRequest) {
        User user = userRepository.findByEmail(httpServletRequest.getUserPrincipal().getName()).orElseThrow();
        user.setShippingLocation(null);
        userRepository.save(user);
    }
    @PostMapping(value = "/billing")
    public LocationAddress addBillingAddress(@RequestBody LocationAddress locationAddress, HttpServletRequest httpServletRequest) {
        User user = userRepository.findByEmail(httpServletRequest.getUserPrincipal().getName()).orElseThrow();
        user.setBillingLocation(locationAddress);
        return userRepository.save(user).getBillingLocation();
    }
    @PutMapping(value = "/billing")
    public LocationAddress updateBillingAddress(@RequestBody LocationAddress locationAddress, HttpServletRequest httpServletRequest) {
        User user = userRepository.findByEmail(httpServletRequest.getUserPrincipal().getName()).orElseThrow();
        user.setBillingLocation(locationAddress);
        return userRepository.save(user).getBillingLocation();
    }
    @DeleteMapping(value = "/billing")
    public void deleteBillingAddress(HttpServletRequest httpServletRequest) {
        User user = userRepository.findByEmail(httpServletRequest.getUserPrincipal().getName()).orElseThrow();
        user.setBillingLocation(null);
        userRepository.save(user);
    }
}