package com.kushi.in.app.service;


import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.model.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookingService {

    Customer createBooking(BookingRequest request);

    List<BookingDTO> getAllBookings();

    Customer updateBookingStatus(Long bookingId, String status, String canceledBy, String cancellationReason);

    void sendBookingNotification(String email, String phoneNumber, String status);

    Customer updateBookingDiscount(Long id, double discount);

    Optional<Customer> getBookingDetailsById(Long id);

    Customer updateBookingInspection(Long bookingId, InspectionUpdateRequest request);

    // Optional: Get bookings specifically by inspection status
    List<Customer> getBookingsByInspectionStatus(String status);

    //to get todays schedule list
    List<BookingDTO> getTodaysBookings();

    Customer updatePaymentStatus(Long bookingId, String paymentStatus);

    Customer adminCreateBooking(AdminBookingCreateRequest req);

    Customer updatePaymentMethod(Long bookingId, String paymentMethod);
}
