// src/main/java/com/kushi/in/app/controller/CustomerController.java
package com.kushi.in.app.controller;


import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.entity.Services;
import com.kushi.in.app.model.CustomerDTO;
import com.kushi.in.app.service.CustomerService;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.kushi.in.app.config.AppConstants.*;
import static com.kushi.in.app.constants.KushiConstants.KUSHI_GLOBAL;

@RestController

@RequestMapping("/api/customers")
@CrossOrigin(origins = { KUSHI_GLOBAL }) // {KUSHI_SERVICES_URL, KUSHI_SERVICES_WWW_URL})
public class CustomerController {

    private final CustomerService customerService;


    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // ===========================
    // Customer Management Endpoints
    // ===========================

    // All customers
    @GetMapping
    public List<CustomerDTO> getAll() {
        return customerService.getAll();
    }

    // Get single customer by bookingId
    @GetMapping("/{bookingId}")
    public ResponseEntity<Customer> getOne(@PathVariable Long bookingId) {
        Customer c = customerService.getById(bookingId);
        return c != null ? ResponseEntity.ok(c) : ResponseEntity.notFound().build();
    }

    // Create new customer
    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        return customerService.create(customer);
    }

    // Update customer
    @PutMapping("/{bookingId}")
    public ResponseEntity<Customer> update(@PathVariable Long bookingId, @RequestBody Customer update) {
        Customer c = customerService.update(bookingId, update);
        return c != null ? ResponseEntity.ok(c) : ResponseEntity.notFound().build();
    }

    // Delete customer
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> delete(@PathVariable Long bookingId) {
        return customerService.delete(bookingId) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Customers by booking status
    @GetMapping("/by-booking-status")
    public ResponseEntity<List<Customer>> getCustomersByBookingStatus(@RequestParam String status) {
        try {
            return ResponseEntity.ok(customerService.getCustomersByBookingStatus(status));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Logged-in customers
    @GetMapping("/logged-in")
    public List<CustomerDTO> getLoggedInCustomers() {
        return customerService.getLoggedInCustomers();
    }

    // Guest customers
    @GetMapping("/guests")
    public List<CustomerDTO> getGuestCustomers() {
        return customerService.getGuestCustomers();
    }

    // Completed bookings
    @GetMapping("/completed")
    public List<CustomerDTO> getCompletedBookings() {
        return customerService.getCompletedBookings();
    }

    // Get bookings by email (for checking Razorpay payment data)
    @GetMapping("/by-email/{email}")
    public ResponseEntity<List<Customer>> getBookingsByEmail(@PathVariable String email) {
        try {
            List<Customer> bookings = customerService.getBookingsByEmail(email);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // ===========================
    // Service Management Endpoints
    // ===========================

    // Add a new service
    // Add a new service
// Controller
    @PostMapping(value = "/add-service", consumes = {"multipart/form-data"})
    public ResponseEntity<?> addService(
            @RequestPart("service") Services services,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            Services saved = customerService.addService(services, imageFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }






    // Get all services - always returns JSON
    @GetMapping(value = "/all-services", produces = "application/json")
    public ResponseEntity<List<Services>> getAllServices() {
        List<Services> services = customerService.getAllServices();
        return ResponseEntity.ok(services);
    }

    // Delete a service by ID
    @DeleteMapping("/delete-service/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        try {
            customerService.deleteService(id);
            return ResponseEntity.ok(Map.of("message", "Service deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete service: " + e.getMessage()));
        }
    }

    // Enable / Disable service
    @PutMapping("/{id}/status")
    public String updateServiceStatus(@PathVariable Long id, @RequestParam String status) {
        return customerService.updateServiceStatus(id, status);
    }

    // Update Service Endpoint (Fix for 404)
    @PutMapping(value = "/update-service/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<?> updateService(
            @PathVariable Long id,
            @RequestPart("service") Services services,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            Services updated = customerService.updateService(id, services, imageFile);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
