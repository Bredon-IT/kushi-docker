package com.kushi.in.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class CustomerDTO {
    private Long booking_id;
    private Integer customer_id;
    private Long userId;
    private String customer_name;
    private String customer_email;
    private String customer_number;
    private String address_line_1;
    private String city;
    private Double totalAmount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime bookingDate;
    private String bookingStatus;
    private String booking_time;
    private String booking_service_name;
    private Double booking_amount;
    private Double discount;
    private Double grand_total;
    private String paymentMethod;
    private String paymentStatus;
    // ✅ ADDED FIELD: Inspection Status (e.g., "completed", "pending", "cancelled", "confirmed")
    private String inspection_status;



    private String address_line_2;
    private String address_line_3;
    // No-arg constructor
    public CustomerDTO() {
    }

    public CustomerDTO(Long booking_id, Integer customer_id, Long userId, String customer_name, String customer_email, String customer_number, String address_line_1, String city, Double totalAmount, LocalDateTime bookingDate, String bookingStatus, String booking_time, String booking_service_name, Double booking_amount, Double discount, Double grand_total, String payment_method, String payment_status, String inspection_status, String address_line_2, String address_line_3) {
        this.booking_id = booking_id;
        this.customer_id = customer_id;
        this.userId = userId;
        this.customer_name = customer_name;
        this.customer_email = customer_email;
        this.customer_number = customer_number;
        this.address_line_1 = address_line_1;
        this.city = city;
        this.totalAmount = totalAmount;
        this.bookingDate = bookingDate;
        this.bookingStatus = bookingStatus;
        this.booking_time = booking_time;
        this.booking_service_name = booking_service_name;
        this.booking_amount = booking_amount;
        this.discount = discount;
        this.grand_total = grand_total;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.inspection_status = inspection_status;
        this.address_line_2 = address_line_2;
        this.address_line_3 = address_line_3;
    }




    // Getters and setters...

    public Long getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getCustomer_number() {
        return customer_number;
    }

    public void setCustomer_number(String customer_number) {
        this.customer_number = customer_number;
    }

    public String getAddress_line_1() {
        return address_line_1;
    }

    public void setAddress_line_1(String address_line_1) {
        this.address_line_1 = address_line_1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double total_amount) {
        this.totalAmount = total_amount;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getBooking_time() {
        return booking_time;
    }

    public void setBooking_time(String booking_time) {
        this.booking_time = booking_time;
    }

    public String getBooking_service_name() {
        return booking_service_name;
    }

    public void setBooking_service_name(String booking_service_name) {
        this.booking_service_name = booking_service_name;
    }

    public Double getBooking_amount() {
        return booking_amount;
    }

    public void setBooking_amount(Double booking_amount) {
        this.booking_amount = booking_amount;
    }

    public Integer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getGrand_total() {
        return grand_total;
    }

    public void setGrand_total(Double grand_total) {
        this.grand_total = grand_total;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getInspection_status() {
        return inspection_status;
    }

    public void setInspection_status(String inspection_status) {
        this.inspection_status = inspection_status;
    }

    public String getAddress_line_2() {
        return address_line_2;
    }

    public void setAddress_line_2(String address_line_2) {
        this.address_line_2 = address_line_2;
    }

    public String getAddress_line_3() {
        return address_line_3;
    }

    public void setAddress_line_3(String address_line_3) {
        this.address_line_3 = address_line_3;
    }
}
