package com.kushi.in.app.controller;

import com.kushi.in.app.dao.CustomerRepository;
import com.kushi.in.app.dao.UserRepository;
import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.entity.User;
import com.kushi.in.app.model.*;
import com.kushi.in.app.service.OrderService;
import com.kushi.in.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
        origins = "https://main.dhtawzq4yzgjo.amplifyapp.com",
        allowCredentials = "true"
)
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final CustomerRepository customerRepository;

    public UserController(
            UserService userService,
            UserRepository userRepository,
            OrderService orderService,
            CustomerRepository customerRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.orderService = orderService;
        this.customerRepository = customerRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest signupRequest) {
        AuthResponse response = userService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody SigninRequest signinRequest) {
        AuthResponse response = userService.signin(signinRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String response = userService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<User> getProfile(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setPhone(updatedUser.getPhone());
        user.setAddress(updatedUser.getAddress());
        user.setCity(updatedUser.getCity());
        user.setPincode(updatedUser.getPincode());

        User saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    // ⭐️ Rating update endpoint
    @PutMapping("/bookings/{id}/rating")
    public ResponseEntity<String> rateBooking(
            @PathVariable Long id,
            @RequestBody RatingRequest request) {
        orderService.updateRatingAndFeedback(id, request);
        return ResponseEntity.ok("Rating and feedback saved successfully");
    }

    // Fetch logged-in user's bookings
    @GetMapping("/bookings/logged-in")
    public ResponseEntity<List<Customer>> getLoggedInBookings(@RequestParam("email") String email) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Ensure user exists
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        List<Customer> bookings = orderService.getBookingsForUserByEmail(email);

        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(bookings);
    }

    // Logged-in user creates a booking
    @PostMapping("/bookings/logged-in/{userId}")
    public ResponseEntity<Customer> createBooking(
            @PathVariable Long userId,
            @RequestBody Customer booking) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        booking.setUser(user);
        booking.setCustomer_email(user.getEmail());

        Customer savedBooking = customerRepository.save(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
    }

    // ⭐ New Public Reviews Endpoint
    @GetMapping("/public/reviews")
    public ResponseEntity<List<Customer>> getPublicReviews() {
        List<Customer> reviews = orderService.getPublishedReviews();

        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reviews); // In production, use DTOs for safety
    }
}
