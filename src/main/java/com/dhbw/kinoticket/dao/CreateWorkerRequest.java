package com.dhbw.kinoticket.dao;

import com.dhbw.kinoticket.entity.User;
import jakarta.validation.constraints.NotEmpty;

public class CreateWorkerRequest {
    @NotEmpty(message = "USER_REQUIRED")
    private User user;
    @NotEmpty(message = "ADMINSTATUS_REQUIRED")
    private boolean isAdmin;
}
