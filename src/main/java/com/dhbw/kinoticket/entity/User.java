package com.dhbw.kinoticket.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "User")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="shippingLocation_id", referencedColumnName = "id", insertable = false, updatable = false)
    private LocationAddress shippingLocation;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="billingLocation_id", referencedColumnName = "id", insertable = false, updatable = false)
    private LocationAddress billingLocation;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}