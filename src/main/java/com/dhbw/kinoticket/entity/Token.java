package com.dhbw.kinoticket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Token {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique=true)
    private String token;
    @Enumerated(EnumType.STRING)
    private TokenType tokenType = TokenType.BEARER;
    private boolean revoked;
    private boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
