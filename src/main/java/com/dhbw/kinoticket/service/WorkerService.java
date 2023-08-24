package com.dhbw.kinoticket.service;

import com.dhbw.kinoticket.entity.Worker;
import com.dhbw.kinoticket.repository.WorkerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
@Service
public class WorkerService {
    private final PasswordEncoder passwordEncoder;
    private final WorkerRepository workerRepository;
    public WorkerService(PasswordEncoder passwordEncoder, WorkerRepository workerRepository) {
        this.passwordEncoder = passwordEncoder;
        this.workerRepository = workerRepository;
    }
    public boolean isUserNameUnique(String userName) {
        return workerRepository.countByUsername(userName) == 0;
    }
    public Worker create(Worker worker) {
        if(!isUserNameUnique(worker.getUsername().toLowerCase())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "EMAIL_NOT_UNIQUE");
        }
        worker.setUsername(worker.getUsername().toLowerCase());
        worker.setPassword(passwordEncoder.encode(worker.getPassword()));
        return workerRepository.save(worker);
    }
    public Worker update(Worker worker) {
        return workerRepository.save(worker);
    }
    public void delete(Long id) {
        workerRepository.deleteById(id);
    }
    public Worker findById(Long id) {
        return workerRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    public Worker findByUsername(String username) {
        return workerRepository.findByUsername(username);
    }
    public Iterable<Worker> findAll() {
        return workerRepository.findAll();
    }
}
