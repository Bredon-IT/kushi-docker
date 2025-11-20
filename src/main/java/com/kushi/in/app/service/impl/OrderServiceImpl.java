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
        customerRepository.syncUserIdsWithEmails();
        return customerRepository.findBookingsByUserEmail(email);
    }

    @Override
    @Transactional
    public Customer createBookingForUser(Customer booking, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        booking.setUser(user);
        booking.setCustomer_email(user.getEmail());

        return customerRepository.save(booking);
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
        return customerRepository.findAllByRatingIsNotNullAndFeedbackIsNotNull();
    }
}
