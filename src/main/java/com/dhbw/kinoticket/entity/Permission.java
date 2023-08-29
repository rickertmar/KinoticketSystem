package com.dhbw.kinoticket.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_DELETE("admin:delete"),
    ADMIN_CREATE("admin:create"),
    WORKER_READ("worker:read"),
    WORKER_UPDATE("worker:update"),
    WORKER_DELETE("worker:delete"),
    WORKER_CREATE("worker:create");
    private final String permission;


}
