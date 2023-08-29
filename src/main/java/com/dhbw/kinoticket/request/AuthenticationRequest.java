package com.dhbw.kinoticket.request;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    private String email;
    String password;
}
