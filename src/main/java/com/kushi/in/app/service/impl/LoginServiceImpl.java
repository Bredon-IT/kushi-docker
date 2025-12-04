package com.kushi.in.app.service.impl;

import com.kushi.in.app.dao.LoginRepository;
import com.kushi.in.app.entity.Login;
import com.kushi.in.app.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginRepository loginRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public Login loginAdmin(String email, String password) {
        return loginRepository.findByEmail(email)
                .filter(user -> user.getEmail().equals(email))      // ✅ case-sensitive email
                .filter(user -> passwordEncoder.matches(password, user.getPassword())) // ✅ BCrypt password verification
                .orElse(null);
    }



    @Override
    public Login getAdminByEmail(String email) {
        return loginRepository.findByEmail(email).orElse(null);
    }

    @Override
    public Login updateAdmin(Long id, Login updatedAdmin) {
        return loginRepository.findById(id).map(existingAdmin -> {
            existingAdmin.setAdminname(updatedAdmin.getAdminname());
            existingAdmin.setEmail(updatedAdmin.getEmail());
            existingAdmin.setPhoneNumber(updatedAdmin.getPhoneNumber());
            // ✅ Only update password if it's provided and not already hashed
            if (updatedAdmin.getPassword() != null && !updatedAdmin.getPassword().isEmpty()) {
                // Check if password is already hashed (BCrypt hashes start with $2a$, $2b$, or $2y$)
                if (!updatedAdmin.getPassword().startsWith("$2")) {
                    existingAdmin.setPassword(passwordEncoder.encode(updatedAdmin.getPassword()));
                } else {
                    existingAdmin.setPassword(updatedAdmin.getPassword());
                }
            }
            existingAdmin.setRole(updatedAdmin.getRole());
            return loginRepository.save(existingAdmin);
        }).orElse(null);
    }


    @Override
    public Login updatePassword(Long adminId, String oldPassword, String newPassword) {
        Login admin = loginRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // ✅ Verify old password with BCrypt
        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // ✅ Hash new password with BCrypt
        admin.setPassword(passwordEncoder.encode(newPassword));
        return loginRepository.save(admin);
    }


    @Override
    public Login addUser(Login login) {
        if (loginRepository.existsByEmail(login.getEmail())) {
            throw new RuntimeException("❌ Email already exists!");
        }
        if (login.getPhoneNumber() != null && loginRepository.existsByPhoneNumber(login.getPhoneNumber())) {
            throw new RuntimeException("❌ Phone number already exists!");
        }
        // ✅ Hash password before saving new user
        if (login.getPassword() != null && !login.getPassword().isEmpty()) {
            login.setPassword(passwordEncoder.encode(login.getPassword()));
        }
        return loginRepository.save(login);
    }

    @Override
    public List<Login> getAllUsers() {
        return loginRepository.findAll();
    }

    @Override
    public void deleteUser(Long adminId) {
        if (!loginRepository.existsById(adminId)) {
            throw new RuntimeException("❌ User not found");
        }
        loginRepository.deleteById(adminId);
    }



    @Override
    public void updatePassword(String email, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("❌ Passwords do not match");
        }

        Login user = loginRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("❌ User not found"));

        // ✅ Hash password with BCrypt
        user.setPassword(passwordEncoder.encode(newPassword));
        loginRepository.save(user);
    }

}

