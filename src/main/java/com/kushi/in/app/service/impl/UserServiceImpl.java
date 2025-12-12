package com.kushi.in.app.service.impl;

import com.kushi.in.app.dao.CustomerRepository;
import com.kushi.in.app.dao.UserRepository;
import com.kushi.in.app.entity.User;
import com.kushi.in.app.model.AuthResponse;
import com.kushi.in.app.model.SigninRequest;
import com.kushi.in.app.model.SignupRequest;
import com.kushi.in.app.model.ForgotPasswordRequest;
import com.kushi.in.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public AuthResponse signup(SignupRequest request) {

        // ✅ Combined validation: email + phone (returns both messages if needed)
        StringBuilder error = new StringBuilder();

        if (userRepository.existsByEmail(request.getEmail())) {
            error.append("Email already exists. ");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            error.append("Phone number already registered.");
        }

        if (error.length() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error.toString().trim());
        }

        // Create new user object
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setFullName(request.getFirstName() + " " + request.getLastName());
        user.setEmail(request.getEmail().toLowerCase()); // lowercase for consistency
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password with BCrypt

        User saved = userRepository.save(user);

        // Update user ID in customer table
        customerRepository.updateUserIdByEmail(saved.getEmail());

        // Sync user_id in tbl_booking_info
        customerRepository.syncUserIdsWithEmails();

        return new AuthResponse(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail()
        );
    }

    @Override
    public AuthResponse signin(SigninRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        // Validate password using BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail()
        );
    }

    @Override
    public String forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        // Hash new password with BCrypt
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Password updated successfully";
    }
}