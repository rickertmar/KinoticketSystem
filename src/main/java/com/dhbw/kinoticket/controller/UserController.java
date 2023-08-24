package com.dhbw.kinoticket.controller;

import com.dhbw.kinoticket.entity.User;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.dhbw.kinoticket.service.UserService;

@RestController()
@CrossOrigin
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("")
    public User createUser(@RequestBody @Valid User userRequest) {
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setBillingLocation(userRequest.getBillingLocation());
        user.setShippingLocation(userRequest.getShippingLocation());
        this.userService.create(user);
        return user;
    }
    @GetMapping("")
    public Iterable<User> getAllUsers() {
        return this.userService.findAll();
    }
}
