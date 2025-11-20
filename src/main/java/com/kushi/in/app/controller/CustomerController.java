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


@RestController

@RequestMapping("/api/customers")
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

    // ===========================
    // Service Management Endpoints
    // ===========================

    // Add a new service
    // Add a new service
// Controller
    @PostMapping(value = "/add-service", consumes = {"multipart/form-data"})
    public ResponseEntity<?> addService(
            @RequestPart("service") Services services,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        try {
            // 1. Handle file upload
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path uploadPath = Paths.get(System.getProperty("user.dir") + "/uploads");
                Files.createDirectories(uploadPath);
                Files.write(uploadPath.resolve(fileName), imageFile.getBytes());

                services.setService_image_url("/uploads/" + fileName); // local file
            } else if (services.getService_image_url() != null && !services.getService_image_url().isEmpty()) {
                // URL provided, keep as-is
            } else {
                services.setService_image_url(null); // no image
            }

            // 2. Save the service
            Services saved = customerService.addService(services);

            // 3. Prepend base URL only for local uploads
            if (saved.getService_image_url() != null && saved.getService_image_url().startsWith("/uploads/")) {
                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                saved.setService_image_url(baseUrl + saved.getService_image_url());
            }

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }



    // ✅ Update service by ID
    @PutMapping(value = "/update-service/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateService(
            @PathVariable Long id,
            @RequestPart("service") Services services,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        try {
            // Handle image update
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path uploadPath = Paths.get(System.getProperty("user.dir") + "/uploads");
                Files.createDirectories(uploadPath);
                Files.write(uploadPath.resolve(fileName), imageFile.getBytes());
                services.setService_image_url("/uploads/" + fileName);
            }

            Services updated = customerService.updateService(id, services);

            // Prepend base URL for local images
            if (updated.getService_image_url() != null && updated.getService_image_url().startsWith("/uploads/")) {
                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                updated.setService_image_url(baseUrl + updated.getService_image_url());
            }

            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update service", "details", e.getMessage()));
        }
    }


    // Enable / Disable service
    @PutMapping("/{id}/status")
    public String updateServiceStatus(@PathVariable Long id, @RequestParam String status) {
        return customerService.updateServiceStatus(id, status);
    }
}
