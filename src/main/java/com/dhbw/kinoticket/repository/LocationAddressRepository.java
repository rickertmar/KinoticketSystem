package com.dhbw.kinoticket.repository;

import com.dhbw.kinoticket.entity.LocationAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationAddressRepository extends JpaRepository<LocationAddress, Long> {

}
