package com.kushi.in.app.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {
    private Long customerId;
    private String customerName;
    private String email;


    public AuthResponse(Long customerId, String customerName, String email) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.email = email;

    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
