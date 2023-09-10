package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.entity.User;
import com.dhbw.kinoticket.repository.LocationAddressRepository;
import com.dhbw.kinoticket.repository.TokenRepository;
import com.dhbw.kinoticket.repository.UserRepository;
import com.dhbw.kinoticket.request.CreateLocationRequest;
import com.dhbw.kinoticket.request.UpdateUserRequest;
import com.dhbw.kinoticket.response.LocationResponse;
import com.dhbw.kinoticket.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final UserRepository userRepository;
    private final LocationAddressRepository locationAddressRepository;
    private final TokenRepository tokenRepository;
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
        User user = userRepository.findById(id).get();
        userRepository.delete(user);
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
    @DeleteMapping(value = "")
    public void deleteSelf(HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        userRepository.delete(user); //and associated tokens with its foreign keys -> cascade option in token entity
    }
    @PutMapping(value = "")
    public ResponseEntity<UserResponse> updateSelf(@RequestBody UpdateUserRequest updateUserRequest, HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        user.setFirstName(updateUserRequest.getFirstName());
        user.setLastName(updateUserRequest.getLastName());
        userRepository.save(user);
        var userResponse = UserResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .billing(user.getBillingLocation())
                .shipping(user.getShippingLocation())
                .build();
        return ResponseEntity.ok(userResponse);
    }
    @PostMapping(value = "/shipping")
    public ResponseEntity<LocationResponse> addShippingAddress(@RequestBody CreateLocationRequest createLocationRequest, HttpServletRequest httpServletRequest) {
        var locationAddress = LocationAddress
                .builder()
                .street(createLocationRequest.getStreet())
                .city(createLocationRequest.getCity())
                .country(createLocationRequest.getCountry())
                .postcode(createLocationRequest.getPostcode())
                .build();
        var location = locationAddressRepository.save(locationAddress);
        User user = userRepository.findByEmail(httpServletRequest.getUserPrincipal().getName()).orElseThrow();
        user.setShippingLocation(location);
        userRepository.save(user);
        var locationResponse = LocationResponse.builder()
                .street(location.getStreet())
                .city(location.getCity())
                .country(location.getCountry())
                .postcode(location.getPostcode())
                .build();
        return ResponseEntity.ok(locationResponse);
    }
    @PutMapping(value = "/shipping")
    public ResponseEntity<LocationResponse> updateShippingAddress(@RequestBody CreateLocationRequest createLocationRequest, HttpServletRequest httpServletRequest) {
        User user = userRepository.findByEmail(httpServletRequest.getUserPrincipal().getName()).orElseThrow();
        LocationAddress location = user.getShippingLocation();
        location.setStreet(createLocationRequest.getStreet());
        location.setCity(createLocationRequest.getCity());
        location.setCountry(createLocationRequest.getCountry());
        location.setPostcode(createLocationRequest.getPostcode());
        locationAddressRepository.save(location);
        user.setShippingLocation(location);
        userRepository.save(user);
        var locationResponse = LocationResponse.builder()
                .street(location.getStreet())
                .city(location.getCity())
                .country(location.getCountry())
                .postcode(location.getPostcode())
                .build();
        return ResponseEntity.ok(locationResponse);
    }
    @DeleteMapping(value = "/shipping")
    public void deleteShippingAddress(HttpServletRequest httpServletRequest) {
        User user = userRepository.findByEmail(httpServletRequest.getUserPrincipal().getName()).orElseThrow();
        LocationAddress locationAddress = user.getShippingLocation();
        locationAddressRepository.delete(locationAddress);
        user.setShippingLocation(null);
        userRepository.save(user);
    }
    @PostMapping(value = "/billing")
    public ResponseEntity<LocationResponse> addBillingAddress(@RequestBody CreateLocationRequest createLocationRequest, HttpServletRequest httpServletRequest) {
        var locationAddress = LocationAddress
                .builder()
                .street(createLocationRequest.getStreet())
                .city(createLocationRequest.getCity())
                .country(createLocationRequest.getCountry())
                .postcode(createLocationRequest.getPostcode())
                .build();
        var location = locationAddressRepository.save(locationAddress);
        User user = userRepository.findByEmail(httpServletRequest.getUserPrincipal().getName()).orElseThrow();
        user.setBillingLocation(location);
        userRepository.save(user);
        var locationResponse = LocationResponse.builder()
                .street(location.getStreet())
                .city(location.getCity())
                .country(location.getCountry())
                .postcode(location.getPostcode())
                .build();
        return ResponseEntity.ok(locationResponse);
    }
    @PutMapping(value = "/billing")
    public ResponseEntity<LocationResponse> updateBillingAddress(@RequestBody CreateLocationRequest createLocationRequest, HttpServletRequest httpServletRequest) {
        User user = userRepository.findByEmail(httpServletRequest.getUserPrincipal().getName()).orElseThrow();
        LocationAddress location = user.getBillingLocation();
        location.setStreet(createLocationRequest.getStreet());
        location.setCity(createLocationRequest.getCity());
        location.setCountry(createLocationRequest.getCountry());
        location.setPostcode(createLocationRequest.getPostcode());
        locationAddressRepository.save(location);
        user.setBillingLocation(location);
        userRepository.save(user);
        var locationResponse = LocationResponse.builder()
                .street(location.getStreet())
                .city(location.getCity())
                .country(location.getCountry())
                .postcode(location.getPostcode())
                .build();
        return ResponseEntity.ok(locationResponse);
    }
    @DeleteMapping(value = "/billing")
    public void deleteBillingAddress(HttpServletRequest httpServletRequest) {
        User user = userRepository.findByEmail(httpServletRequest.getUserPrincipal().getName()).orElseThrow();
        user.setBillingLocation(null);
        userRepository.save(user);
    }
}