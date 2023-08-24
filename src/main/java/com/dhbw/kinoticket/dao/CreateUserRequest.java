package com.dhbw.kinoticket.dao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CreateUserRequest {
    @NotNull
    @NotEmpty(message="FIRST_NAME_REQUIRED")
    private String email;
    @NotNull
    @NotEmpty(message="PASSWORD_REQUIRED")
    private String password;
    @NotNull
    @NotEmpty(message="FIRST_NAME_REQUIRED")
    private String firstName;
    @NotNull
    @NotEmpty(message="LAST_NAME_REQUIRED")
    private String lastName;
}
