package com.kushi.in.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_booking_info")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long booking_id;

    private Integer customer_id;
    private String customer_name;

    @Column(name = "customer_email")
    private String customer_email;

    private String customer_number;
    private String address_line_1;
    private String address_line_2;
    private String address_line_3;

    private Double booking_amount;

    @Column(name = "total_amount")
    private Double totalAmount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookingDate;

    private String booking_service_name;

    @Column(name = "booking_status")
    private String bookingStatus = "pending";

    private String booking_time;
    private String city;
    private String zip_code;
    private String confirmation_date;
    private String created_by;
    private String created_date;

    // Payment fields
    private String payment_method;
    private String payment_status;
    private String reference_details;
    private String reference_name;
    private String remarks;

    private String updated_by;
    private String updated_date;
    private String worker_assign;
    private String site_visit;

    private String feedback;

    @Column(name = "canceled_by")
    private String canceledBy;

    @Column(name = "service_id")
    private Long service_id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", referencedColumnName = "service_id", insertable = false, updatable = false)
    private Services services;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private Double discount;

    private Double grand_total;

    @Column(columnDefinition = "LONGTEXT")
    private String cancellation_reason;

    @Column(name = "service_category")
    private String serviceCategory;

    private Integer rating;

    // ============================
    // Getters & Setters (cleaned)
    // ============================

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getCanceledBy() {
        return canceledBy;
    }

    public void setCanceledBy(String canceledBy) {
        this.canceledBy = canceledBy;
    }

    public String getCancellation_reason() {
        return cancellation_reason;
    }

    public void setCancellation_reason(String cancellation_reason) {
        this.cancellation_reason = cancellation_reason;
    }
}
