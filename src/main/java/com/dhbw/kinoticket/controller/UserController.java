package com.dhbw.kinoticket.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    @GetMapping("")
    @PreAuthorize("hasAuthority('admin:read')")
    public String test(){
        return "only Admin can see this page";
    }
}