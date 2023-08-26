package com.dhbw.kinoticket.controller;


import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.service.LocationAddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/locationAddresses")
public class LocationAddressController {
    private final LocationAddressService locationAddressService;

    public LocationAddressController(LocationAddressService locationAddressService) {
        this.locationAddressService = locationAddressService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createLocationAddress(@Valid @RequestBody LocationAddress locationAddressRequest) {
        try {
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.setStreet(locationAddressRequest.getStreet());
            locationAddress.setCity(locationAddressRequest.getCity());
            locationAddress.setCountry(locationAddressRequest.getCountry());
            locationAddress.setPostcode(locationAddressRequest.getPostcode());

            locationAddress = locationAddressService.createLocationAddress(locationAddress);

            return new ResponseEntity<>(locationAddress, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create location address", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Get Response of specified cinema by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getLocationAddressById(@PathVariable Long id) {
        LocationAddress locationAddress = locationAddressService.findById(id);
        if (locationAddress != null) {
            return new ResponseEntity<>(locationAddress, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Location address not found", HttpStatus.NOT_FOUND);
        }
    }
}
