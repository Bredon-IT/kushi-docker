package com.kushi.in.app.service.impl;

import com.kushi.in.app.dao.CustomerRepository;
import com.kushi.in.app.dao.UserRepository;
import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.entity.User;
import com.kushi.in.app.model.RatingRequest;
import com.kushi.in.app.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OrderServiceImpl implements OrderService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public List<Customer> getBookingsForUserByEmail(String email) {
        // Optional: sync existing bookings if needed
        customerRepository.syncUserIdsWithEmails();
        return customerRepository.findBookingsByUserEmail(email);
    }

    @Override
    @Transactional
    public Customer createBookingForUser(Customer booking, Long userId) {
        // Fetch logged-in user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Assign user to booking automatically
        booking.setUser(user);
        booking.setCustomer_email(user.getEmail()); // ensure email is correct

        // Save booking
        Customer savedBooking = customerRepository.save(booking);

        return savedBooking;
    }

    @Override
    public void updateRatingAndFeedback(Long bookingId, RatingRequest request) {
        Customer booking = customerRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setRating(request.getRating());
        booking.setFeedback(request.getFeedback());
        customerRepository.save(booking);
    }

    @Override
    @Transactional
    public List<Customer> getPublishedReviews() {
        // Calls the new repository method to fetch only completed reviews
        return customerRepository.findAllByRatingIsNotNullAndFeedbackIsNotNull();
    }
}
