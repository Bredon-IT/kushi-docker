package com.kushi.in.app.service;

import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.model.RatingRequest;

import java.util.List;

public interface OrderService {

    // Fetch bookings for logged-in user
    List<Customer> getBookingsForUserByEmail(String email);

    // Create a booking for a logged-in user
    Customer createBookingForUser(Customer booking, Long userId);

    // Update rating & feedback
    void updateRatingAndFeedback(Long bookingId, RatingRequest request);

    // Fetch only published reviews (rating and feedback not null)
    List<Customer> getPublishedReviews();
}
