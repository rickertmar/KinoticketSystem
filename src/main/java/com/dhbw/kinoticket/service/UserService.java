package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.entity.User;
import com.dhbw.kinoticket.repository.LocationAddressRepository;
import com.dhbw.kinoticket.repository.UserRepository;
import com.dhbw.kinoticket.request.CreateLocationRequest;
import com.dhbw.kinoticket.request.UpdateUserRequest;
import com.dhbw.kinoticket.response.LocationResponse;
import com.dhbw.kinoticket.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Component
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LocationAddressRepository locationAddressRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with E-Mail: " + email));
    }

    public UserResponse updateUser(UpdateUserRequest updateUserRequest, User user) {
        user.setFirstName(updateUserRequest.getFirstName());
        user.setLastName(updateUserRequest.getLastName());
        userRepository.save(user);
        return userResponseBuilder(user);
    }

    public UserResponse userResponseBuilder(User user) {
        return UserResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .billing(user.getBillingLocation())
                .shipping(user.getShippingLocation())
                .build();
    }

    public LocationResponse saveShippingLocation(CreateLocationRequest locationRequest, User user) {
        user.setShippingLocation(locationAddressBuilder(locationRequest));
        userRepository.save(user);
        return locationResponseBuilder(locationAddressBuilder(locationRequest));
    }

    public LocationResponse updateShippingLocation(CreateLocationRequest createLocationRequest, User user) {
        LocationAddress location = user.getShippingLocation();
        location.setStreet(createLocationRequest.getStreet());
        location.setCity(createLocationRequest.getCity());
        location.setCountry(createLocationRequest.getCountry());
        location.setPostcode(createLocationRequest.getPostcode());
        locationAddressRepository.save(location);
        user.setShippingLocation(location);
        userRepository.save(user);
        return locationResponseBuilder(user.getShippingLocation());
    }

    public void deleteShippingLocation(User user) {
        LocationAddress locationAddress = user.getShippingLocation();
        locationAddressRepository.delete(locationAddress);
        user.setShippingLocation(null);
        userRepository.save(user);
    }

    public LocationResponse saveBillingLocation(CreateLocationRequest locationRequest, User user) {
        user.setBillingLocation(locationAddressBuilder(locationRequest));
        userRepository.save(user);
        return locationResponseBuilder(locationAddressBuilder(locationRequest));
    }

    public LocationResponse updateBillingLocation(CreateLocationRequest createLocationRequest, User user) {
        LocationAddress location = user.getBillingLocation();
        location.setStreet(createLocationRequest.getStreet());
        location.setCity(createLocationRequest.getCity());
        location.setCountry(createLocationRequest.getCountry());
        location.setPostcode(createLocationRequest.getPostcode());
        locationAddressRepository.save(location);
        user.setBillingLocation(location);
        userRepository.save(user);
        return locationResponseBuilder(user.getBillingLocation());
    }

    public void deleteBillingLocation(User user) {
        LocationAddress locationAddress = user.getBillingLocation();
        locationAddressRepository.delete(locationAddress);
        user.setBillingLocation(null);
        userRepository.save(user);
    }

    public LocationResponse locationResponseBuilder(LocationAddress locationAddress) {
        return LocationResponse.builder()
                .street(locationAddress.getStreet())
                .city(locationAddress.getCity())
                .country(locationAddress.getCountry())
                .postcode(locationAddress.getPostcode())
                .build();
    }

    public LocationAddress locationAddressBuilder(CreateLocationRequest createLocationRequest) {
        return LocationAddress
                .builder()
                .street(createLocationRequest.getStreet())
                .city(createLocationRequest.getCity())
                .country(createLocationRequest.getCountry())
                .postcode(createLocationRequest.getPostcode())
                .build();
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
