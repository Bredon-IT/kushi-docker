
package com.kushi.in.app.model;

import lombok.Data;

@Data
public class AdminBookingCreateRequest {

    private String customerName;
    private String customerEmail;
    private String customerNumber;

    private String addressLine1;
    private String city;
    private String pincode;

    private String bookingServiceName;

    private String bookingDate; // "2025-01-21"
    private String bookingTime; // "10:30 AM"

    private Double bookingAmount;
    private String remarks;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getBookingServiceName() {
        return bookingServiceName;
    }

    public void setBookingServiceName(String bookingServiceName) {
        this.bookingServiceName = bookingServiceName;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public Double getBookingAmount() {
        return bookingAmount;
    }

    public void setBookingAmount(Double bookingAmount) {
        this.bookingAmount = bookingAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
