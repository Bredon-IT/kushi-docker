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

    // ⭐ New fields
    private String payment_method;
    private String payment_status;
    private String city;
    private String address;

    private String site_visit;        // merged correctly
    private List<String> worker_assign;
    private String canceledBy;

    private Double discount;
    private Double grand_total;

    private String cancellation_reason;   // merged correctly

    // Getters & Setters (Lombok @Data will auto-create them)
}
