package com.dhbw.kinoticket.response;

import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    @JsonProperty("email")
    private String email;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("billing")
    private LocationAddress billing;
    @JsonProperty("shipping")
    private LocationAddress shipping;
}
