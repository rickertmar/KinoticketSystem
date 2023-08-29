package com.dhbw.kinoticket.service;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LocationAddressService {
    private final LocationAddressRepository locationAddressRepository;

    public LocationAddressService(LocationAddressRepository locationAddressRepository) {
        this.locationAddressRepository = locationAddressRepository;
    }

    //Create new location address
    public LocationAddress createLocationAddress(@Valid LocationAddress locationAddress) {
        return locationAddressRepository.save(locationAddress);
    }

    //Update existing location address
    public LocationAddress updateLocationAddress(@Valid LocationAddress locationAddress, Long id) {
        LocationAddress existingLocationAddress = findById(id);
        if (existingLocationAddress == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "LOCATION_ADDRESS_NOT_FOUND");
        }
        existingLocationAddress.setStreet(locationAddress.getStreet());
        existingLocationAddress.setCity(locationAddress.getCity());
        existingLocationAddress.setCountry(locationAddress.getCountry());
        existingLocationAddress.setPostcode(locationAddress.getPostcode());
        return locationAddressRepository.save(existingLocationAddress);
    }

    //Find existing Cinema by id
    public LocationAddress findById(Long id) {
        return locationAddressRepository.findById(id).orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND, "LOCATION_ADDRESS_NOT_FOUND"));
    }

    //Delete LocationAddress by id
    public void deleteLocationAddress(Long id) {
        locationAddressRepository.deleteById(id);
    }

    //Call all LocationAddresses
    public Iterable<LocationAddress> findAll() {
        return locationAddressRepository.findAll();
    }
}
