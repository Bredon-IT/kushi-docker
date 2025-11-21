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
        origins = "https://main.dhtawzq4yzgjo.amplifyapp.com",  // Your React app URL
        allowCredentials = "true"
)

public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final OrderService orderService;

    private final CustomerRepository customerRepository; // ✅ Add this

    public UserController(UserService userService,
                          UserRepository userRepository,
                          OrderService orderService,
                          CustomerRepository customerRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.orderService = orderService;
        this.customerRepository = customerRepository; // ✅ Initialize
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


    // ✅ Fetch logged-in user's bookings based on email passed as query param
    // Fetch logged-in user's bookings based on email passed as query param
    @GetMapping("/bookings/logged-in")
    public ResponseEntity<List<Customer>> getLoggedInBookings(@RequestParam("email") String email) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().build(); // 400 if email not provided
        }

        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Fetch bookings for this user
        List<Customer> bookings = orderService.getBookingsForUserByEmail(email);

        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 if no bookings found
        }

        return ResponseEntity.ok(bookings);
    }



    // ✅ Create a new booking for logged-in user
    @PostMapping("/bookings/logged-in/{userId}")
    public ResponseEntity<Customer> createBooking(@PathVariable Long userId, @RequestBody Customer booking) {
        // Set the user for this booking
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        booking.setUser(user);
        booking.setCustomer_email(user.getEmail());

        Customer savedBooking = customerRepository.save(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
    }

    @GetMapping("/public/reviews")
    public ResponseEntity<List<Customer>> getPublicReviews() {
        // 1. Create this new method in CustomerRepository or OrderService
        List<Customer> reviews = orderService.getPublishedReviews();

        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // 2. IMPORTANT: Use a DTO (Data Transfer Object) here instead of returning
        //    the full Customer entity to prevent exposing sensitive data.
        return ResponseEntity.ok(reviews); // Placeholder, use DTO in production
    }

}




