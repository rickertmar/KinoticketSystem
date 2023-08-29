package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.Cinema;
import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.repository.CinemaRepository;
import com.dhbw.kinoticket.repository.LocationAddressRepository;
import com.dhbw.kinoticket.request.CreateCinemaRequest;
import com.dhbw.kinoticket.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cinemas")
public class CinemaController {
    private final CinemaService cinemaService;
    private final CinemaRepository cinemaRepository;
    private final LocationAddressRepository locationAddressRepository;

    //Create Cinema
    @PreAuthorize("hasAuthority('admin:create')")
    @PostMapping(value = "")
    public ResponseEntity<?> createCinema(@RequestBody CreateCinemaRequest createCinemaRequest) {
        var locationAddress = LocationAddress
                .builder()
                .street(createCinemaRequest.getStreet())
                .city(createCinemaRequest.getCity())
                .country(createCinemaRequest.getCountry())
                .postcode(createCinemaRequest.getPostcode())
                .build();
        var createdLocation = locationAddressRepository.save(locationAddress);
        Cinema cinema = Cinema
                .builder()
                .name(createCinemaRequest.getName())
                .build();
        cinema.setLocationAddress(createdLocation);
        var createdCinema = cinemaRepository.save(cinema);
        return new ResponseEntity<>(createdCinema, HttpStatus.CREATED);
    }

    //Get Response of specified cinema by id
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getCinemaById(@PathVariable Long id) {
        Cinema cinema = cinemaService.findById(id);
        if (cinema != null) {
            return ResponseEntity.ok(cinema);
        } else {
            return new ResponseEntity<>("Cinema not found.", HttpStatus.NOT_FOUND);
        }
    }

    //Delete specific cinema
    @PreAuthorize("hasAuthority('admin:delete')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCinema(@PathVariable Long id) {
        cinemaService.deleteCinema(id);
        return new ResponseEntity<>("Cinema deleted.", HttpStatus.OK);
    }


    // ----------------------------------------------------------------
    //LocationAddress of Cinema
    //Add location address to specific cinema with name
    /*@PostMapping("/location")
    public ResponseEntity<?> addLocationAddress(@Valid @RequestBody String cinemaName,
                                                @Valid @RequestBody CreateLocationRequest createLocationRequest) {
        try {
            var locationAddress = LocationAddress
                    .builder()
                    .street(createLocationRequest.getStreet())
                    .city(createLocationRequest.getCity())
                    .country(createLocationRequest.getCountry())
                    .postcode(createLocationRequest.getPostcode())
                    .build();
            var location = locationAddressRepository.save(locationAddress);
            Cinema cinema = cinemaRepository.findByName(cinemaName);
            cinema.setLocationAddress(locationAddress);
            return new ResponseEntity<>(cinemaRepository.save(cinema).getLocationAddress(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add location.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/

    // ----------------------------------------------------------------
    //Update location address of specific cinema
    @PutMapping(value = "/location")
    public ResponseEntity<?> updateLocationAddress(@RequestBody String cinemaName,
                                                   @RequestBody LocationAddress locationAddress) {
        try {
            Cinema cinema = cinemaRepository.findByName(cinemaName);
            cinema.setLocationAddress(locationAddress);
            return new ResponseEntity<>(cinemaRepository.save(cinema).getLocationAddress(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("LocationAddress or Cinema not found", HttpStatus.NOT_FOUND);
        }
    }
}

/*
Create Cinema json format
{
  "name": "TestCinema",
  "locationAddress": {
    "street": "Teststraße 1",
    "city": "Teststadt",
    "country": "Testland",
    "postcode": "12345"
  }
}
 */