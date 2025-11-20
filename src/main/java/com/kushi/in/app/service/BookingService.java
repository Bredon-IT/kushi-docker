package com.kushi.in.app.service;

import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.model.BookingDTO;
import com.kushi.in.app.model.BookingRequest;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    Customer createBooking(BookingRequest request);

    List<BookingDTO> getAllBookings();

    // Updated method (combined both versions)
    Customer updateBookingStatus(Long bookingId, String status, String canceledBy, String cancellationReason);

    // Common method
    void sendBookingNotification(String email, String phoneNumber, String status);

    // Updated common method (use unified version)
    Customer updateBookingDiscount(Long bookingId, double discount);

    // Added method from HEAD version
    Optional<Customer> getBookingDetailsById(Long id);
}
