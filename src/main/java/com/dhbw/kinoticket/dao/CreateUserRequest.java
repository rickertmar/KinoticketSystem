package com.dhbw.kinoticket.dao;

import jakarta.validation.constraints.NotEmpty;

public class CreateUserRequest {
    @NotEmpty(message="FIRST_NAME_REQUIRED")
    private String email;
    @NotEmpty(message="PASSWORD_REQUIRED")
    private String password;
    @NotEmpty(message="FIRST_NAME_REQUIRED")
    private String firstName;
    @NotEmpty(message="LAST_NAME_REQUIRED")
    private String lastName;
}
