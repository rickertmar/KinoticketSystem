package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.Worker;
import org.springframework.web.bind.annotation.*;
import com.dhbw.kinoticket.service.WorkerService;

@RestController()
@CrossOrigin
@RequestMapping(value = "/worker")
public class WorkerController {
    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }
    @PostMapping(value = "")
    public Worker createWorker(Worker workerRequest) {
        Worker worker = new Worker();
        worker.setUsername(workerRequest.getUsername());
        worker.setUsername("test");
        worker.setPassword(workerRequest.getPassword());
        worker.setAdmin(workerRequest.isAdmin());
        this.workerService.create(worker);
        return worker;
    }
    @GetMapping(value = "")
    public Iterable<Worker> getAllWorkers() {
        return this.workerService.findAll();
    }
}
