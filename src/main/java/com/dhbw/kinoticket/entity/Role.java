package com.dhbw.kinoticket.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.dhbw.kinoticket.entity.Permission.*;
@Getter
@RequiredArgsConstructor
public enum Role {
    USER(Collections.emptySet()),
    ADMIN(Set.of(ADMIN_READ, ADMIN_UPDATE, ADMIN_DELETE, ADMIN_CREATE, WORKER_READ, WORKER_UPDATE, WORKER_DELETE, WORKER_CREATE)),
    WORKER(Set.of(WORKER_READ, WORKER_UPDATE, WORKER_DELETE, WORKER_CREATE));
    private final Set<Permission> permissions;
    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = new java.util.ArrayList<>(getPermissions().stream().map((permission) -> new SimpleGrantedAuthority(permission.getPermission())).toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
