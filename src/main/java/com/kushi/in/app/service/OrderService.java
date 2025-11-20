package com.kushi.in.app.service;

import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.model.RatingRequest;

import java.util.List;

public interface OrderService {

    // Fetch bookings for logged-in user
    List<Customer> getBookingsForUserByEmail(String email);

    // Optional: create a booking for a logged-in user
    Customer createBookingForUser(Customer booking, Long userId);


    void updateRatingAndFeedback(Long bookingId, RatingRequest request);
<<<<<<< HEAD

    List<Customer> getPublishedReviews();
=======
>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788
}
