package com.kushi.in.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class InvoiceDTO {

    private Long booking_id;
    private Double booking_amount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookingDate;

    private String city;
    private Integer customer_id;
    private String customer_name;
    private String customer_email;
    private String customer_number;
    private String address_line_1;

    private Double totalAmount;
    private String worker_assign;

    private Long service_id;
    private Double service_cost;
    private String service_description;
    private String service_details;
    private String service_name;
    private String service_type;

    private String booking_service_name;
    private String bookingStatus;

    // NEW FIELDS (correct from HEAD)
    private Double discount;
    private String payment_status;
    private String payment_method;
    private Double grand_total;

    // ---------------- GETTERS & SETTERS ----------------

    public Long getBooking_id() { return booking_id; }
    public void setBooking_id(Long booking_id) { this.booking_id = booking_id; }

    public Double getBooking_amount() { return booking_amount; }
    public void setBooking_amount(Double booking_amount) { this.booking_amount = booking_amount; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Integer getCustomer_id() { return customer_id; }
    public void setCustomer_id(Integer customer_id) { this.customer_id = customer_id; }

    public String getCustomer_name() { return customer_name; }
    public void setCustomer_name(String customer_name) { this.customer_name = customer_name; }

    public String getCustomer_email() { return customer_email; }
    public void setCustomer_email(String customer_email) { this.customer_email = customer_email; }

    public String getCustomer_number() { return customer_number; }
    public void setCustomer_number(String customer_number) { this.customer_number = customer_number; }

    public String getAddress_line_1() { return address_line_1; }
    public void setAddress_line_1(String address_line_1) { this.address_line_1 = address_line_1; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getWorker_assign() { return worker_assign; }
    public void setWorker_assign(String worker_assign) { this.worker_assign = worker_assign; }

    public Long getService_id() { return service_id; }
    public void setService_id(Long service_id) { this.service_id = service_id; }

    public Double getService_cost() { return service_cost; }
    public void setService_cost(Double service_cost) { this.service_cost = service_cost; }

    public String getService_description() { return service_description; }
    public void setService_description(String service_description) { this.service_description = service_description; }

    public String getService_details() { return service_details; }
    public void setService_details(String service_details) { this.service_details = service_details; }

    public String getService_name() { return service_name; }
    public void setService_name(String service_name) { this.service_name = service_name; }

    public String getService_type() { return service_type; }
    public void setService_type(String service_type) { this.service_type = service_type; }

    public String getBooking_service_name() { return booking_service_name; }
    public void setBooking_service_name(String booking_service_name) { this.booking_service_name = booking_service_name; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public String getPayment_status() { return payment_status; }
    public void setPayment_status(String payment_status) { this.payment_status = payment_status; }

    public String getPayment_method() { return payment_method; }
    public void setPayment_method(String payment_method) { this.payment_method = payment_method; }

    public Double getGrand_total() { return grand_total; }
    public void setGrand_total(Double grand_total) { this.grand_total = grand_total; }
}
