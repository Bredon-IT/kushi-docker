package com.kushi.in.app.model;

import lombok.Data;

@Data
public class InspectionUpdateRequest {

    private String inspection_status; // (completed, pending, cancelled, confirmed)
    private Double booking_amount;    // Admin updates this in real-time (new base amount)
    private String bookingStatus;     // The main booking status
    private String site_visit;        // (completed/not completed)
    private String worker_assign;     // Comma-separated list of worker IDs/names
    private Double discount;          // New discount value

    public String getInspection_status() {
        return inspection_status;
    }

    public void setInspection_status(String inspection_status) {
        this.inspection_status = inspection_status;
    }

    public Double getBooking_amount() {
        return booking_amount;
    }

    public void setBooking_amount(Double booking_amount) {
        this.booking_amount = booking_amount;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getSite_visit() {
        return site_visit;
    }

    public void setSite_visit(String site_visit) {
        this.site_visit = site_visit;
    }

    public String getWorker_assign() {
        return worker_assign;
    }

    public void setWorker_assign(String worker_assign) {
        this.worker_assign = worker_assign;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }
}
