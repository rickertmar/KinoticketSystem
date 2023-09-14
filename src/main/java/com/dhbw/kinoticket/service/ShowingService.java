package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Movie;
import com.dhbw.kinoticket.entity.Showing;
import com.dhbw.kinoticket.repository.ShowingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
@RequiredArgsConstructor
public class ShowingService {

    @Autowired
    private final ShowingRepository showingRepository;

    // Get all showings
    public List<Showing> getAllShowings() {
        return showingRepository.findAll();
    }

    // Get showing by id
    public Showing getShowingById(Long id) {
        return showingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Showing not found with ID: " + id));
    }
}
