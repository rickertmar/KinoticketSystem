package com.dhbw.kinoticket.dao;

import com.dhbw.kinoticket.entity.LocationAddress;
import com.dhbw.kinoticket.entity.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCustomerRequest {
    @NotEmpty(message = "USER_REQUIRED")
    private User user;
    @NotEmpty(message ="BILLING_LOCATION_REQUIRED")
    private LocationAddress billingLocation;
    @NotEmpty(message ="SHIPPING_LOCATION_REQUIRED")
    private LocationAddress shippingLocation;
}
