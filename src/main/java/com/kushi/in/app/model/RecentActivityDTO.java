package com.kushi.in.app.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecentActivityDTO {

    private Long booking_id;
    private String customer_name;
    private String bookingStatus;
    private String booking_service_name;
    private LocalDateTime bookingDate;

    // ⭐ New fields for frontend display
    private String message;
    private String formattedTime;

    // Getters & Setters
    public Long getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getBooking_service_name() {
        return booking_service_name;
    }

    public void setBooking_service_name(String booking_service_name) {
        this.booking_service_name = booking_service_name;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }
}