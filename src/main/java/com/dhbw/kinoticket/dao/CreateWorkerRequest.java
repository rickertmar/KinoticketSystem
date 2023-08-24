package com.dhbw.kinoticket.dao;

import com.dhbw.kinoticket.entity.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CreateWorkerRequest {
    @NotNull
    @NotEmpty(message = "USERNAME_REQUIRED")
    private String userName;
    @NotNull
    @NotEmpty(message = "PASSWORD_REQUIRED")
    private String password;
    @NotNull
    @NotEmpty(message = "ADMINSTATUS_REQUIRED")
    private boolean isAdmin;
}
