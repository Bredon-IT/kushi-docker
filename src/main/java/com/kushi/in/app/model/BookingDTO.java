package com.kushi.in.app.model;

import lombok.Data;

import java.util.List;

@Data
public class BookingDTO {
    private Long booking_id;
    private String customer_name;
    private String customer_email;
    private String customer_number;
    private String booking_service_name;
    private Double booking_amount;
    private String booking_date;
    private String booking_time;
    private String bookingStatus;

    // ⭐️ NEW FIELD
    private String payment_method;

    private String payment_status;
    private String city;
    private String address;

<<<<<<< HEAD
    private String site_visit;
=======
>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788
    private List<String> worker_assign; // NEW
    private String canceledBy;

    private Double discount;
    private Double grand_total;

<<<<<<< HEAD
    private String cancellation_reason;

=======
>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788
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

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public String getBooking_time() {
        return booking_time;
    }

    public void setBooking_time(String booking_time) {
        this.booking_time = booking_time;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    // Getter and Setter for the new payment_method field
    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

<<<<<<< HEAD
    public String getSite_visit() {
        return site_visit;
    }

    public void setSite_visit(String site_visit) {
        this.site_visit = site_visit;
    }
=======
>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788

    public List<String> getWorker_assign() {
        return worker_assign;
    }

    public void setWorker_assign(List<String> worker_assign) {
        this.worker_assign = worker_assign;
    }

    public String getCanceledBy() {
        return canceledBy;
    }

    public void setCanceledBy(String canceledBy) {
        this.canceledBy = canceledBy;
    }
<<<<<<< HEAD

    public String getCancellation_reason() {
        return cancellation_reason;
    }

    public void setCancellation_reason(String cancellation_reason) {
        this.cancellation_reason = cancellation_reason;
    }
}
=======
}
>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788
