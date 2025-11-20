package com.kushi.in.app.controller;

import com.kushi.in.app.entity.Login;
import com.kushi.in.app.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "https://main.dhtawzq4yzgjo.amplifyapp.com", allowCredentials = "true") // Allow from any frontend
public class LoginController {

    @Autowired
    private LoginService loginService;

    // ✅ Login
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginAdmin(@RequestBody Login login) {
        Login loggedInAdmin = loginService.loginAdmin(login.getEmail(), login.getPassword());
        if (loggedInAdmin != null) {
            return ResponseEntity.ok(loggedInAdmin);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // Fetch admin by email
    @GetMapping(value = "/me/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAdminByEmail(@PathVariable String email) {
        Login admin = loginService.getAdminByEmail(email);
        if (admin != null) {
            return ResponseEntity.ok(admin);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Admin not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping(value = "/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAdmin(@PathVariable Long id, @RequestBody Login updatedAdmin) {
        Login admin = loginService.updateAdmin(id, updatedAdmin);
        if (admin != null) {
            return ResponseEntity.ok(admin);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Admin not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping(value = "/update-password/{adminId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePassword(@PathVariable Long adminId,
                                            @RequestParam String oldPassword,
                                            @RequestParam String newPassword) {
        Map<String, String> response = new HashMap<>();
        try {
            loginService.updatePassword(adminId, oldPassword, newPassword);
            response.put("message", "Password updated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ Add a new user
    @PostMapping(value = "/add-user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addUser(@RequestBody Login login, @RequestParam Long adminId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if(adminId != 1) {
                throw new RuntimeException("❌ Only the first admin can add users!");
            }
            Login addedUser = loginService.addUser(login);
            response.put("user", addedUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    // ✅ Get all users
    @GetMapping(value = "/all-users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Login>> getAllUsers() {
        return ResponseEntity.ok(loginService.getAllUsers());
    }

    @DeleteMapping(value = "/delete-user/{targetAdminId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUser(@PathVariable Long targetAdminId, @RequestParam Long adminId) {
        Map<String, String> response = new HashMap<>();
        try {
            if(adminId != 1) {
                throw new RuntimeException("❌ Only the first admin can delete users!");
            }
            loginService.deleteUser(targetAdminId);
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @PostMapping(value = "/forgot-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        try {
            String email = request.get("email");
            String newPassword = request.get("newPassword");
            String confirmPassword = request.get("confirmPassword");

            loginService.updatePassword(email, newPassword, confirmPassword);
            response.put("message", "✅ Password updated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
