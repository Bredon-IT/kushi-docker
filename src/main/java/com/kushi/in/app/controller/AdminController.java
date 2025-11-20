package com.kushi.in.app.controller;

import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.model.CustomerDTO;
import com.kushi.in.app.model.InvoiceDTO;
import com.kushi.in.app.model.ServiceDTO;
import com.kushi.in.app.service.AdminService;
import com.kushi.in.app.service.CustomerService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "https://main.dhtawzq4yzgjo.amplifyapp.com")
public class AdminController {

    private final AdminService adminService;
    private final CustomerService customerService;

    public AdminController(AdminService adminService, CustomerService customerService) {
        this.adminService = adminService;
        this.customerService = customerService;
    }

    // =======================
    // 📌 Booking Management
    // =======================

    @GetMapping("/all-bookings")
    public List<Customer> getAllBookings() {
        return adminService.getAllBookings();
    }

    @PostMapping("/new-booking")
    public Customer createBooking(@RequestBody Customer customer) {
        return adminService.saveBooking(customer);
    }

    // Assign workers
    @PutMapping("/{id}/assign-worker")
    public ResponseEntity<String> assignWorker(
            @PathVariable("id") Long bookingId,
            @RequestBody Map<String, String> body) {

        String workername = body.get("workername");
        adminService.assignWorker(bookingId, workername);

        return ResponseEntity.ok("Worker assigned successfully");
    }

    // =======================
    // 📌 Booking Stats
    // =======================

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getBookingStatistics(
            @RequestParam(value = "timePeriod", defaultValue = "all-time") String timePeriod) {

        try {
            Map<String, Object> stats = adminService.getbookingStatistics(timePeriod);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getBookingOverview(
            @RequestParam(value = "timePeriod", defaultValue = "all-time") String timePeriod) {

        try {
            Map<String, Object> overview = adminService.getOverview(timePeriod);
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/today-bookings")
    public ResponseEntity<Long> getTodayBookings() {
        return ResponseEntity.ok(adminService.getTodayBookings());
    }

    @GetMapping("/pending-approvals")
    public ResponseEntity<Long> getPendingApprovals() {
        return ResponseEntity.ok(adminService.getPendingApprovals());
    }

    @GetMapping("/recent-bookings")
    public ResponseEntity<List<Customer>> getRecentBookingsByDate() {
        return ResponseEntity.ok(adminService.getRecentBookingsByDate());
    }

    @GetMapping("/visit-status")
    public List<String> getVisitStatuses() {
        return adminService.getVisitStatuses();
    }

    @PutMapping("/update")
    public List<String> updateVisitStatuses() {
        return adminService.updateVisitStatuses();
    }

    @GetMapping("/revenue-by-service")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByService() {
        try {
            return ResponseEntity.ok(adminService.getRevenueByService());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    // =======================
    // 📌 Top Customers & Services
    // =======================

    @GetMapping("/top-booked-customers")
    public ResponseEntity<List<CustomerDTO>> getTopBookedCustomers() {
        return ResponseEntity.ok(adminService.getTopBookedCustomers());
    }

    @GetMapping("/top-services")
    public ResponseEntity<Map<String, Object>> getTopServices() {
        Map<String, Object> response = new HashMap<>();
        response.put("topServices", adminService.getTopServices());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-rated-services")
    public ResponseEntity<List<ServiceDTO>> getTopRatedServices() {
        return ResponseEntity.ok(adminService.getTopRatedServices());
    }

    // =======================
    // 📌 Invoice & Reports
    // =======================

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        List<InvoiceDTO> invoices = adminService.getAllInvoices();

        List<InvoiceDTO> completedInvoices = invoices.stream()
                .filter(invoice -> "completed".equalsIgnoreCase(invoice.getBookingStatus()))
                .toList();

        if (completedInvoices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(completedInvoices);
    }

    @GetMapping(value = "/service-report/csv", produces = "text/csv")
    public void downloadServiceReportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=service_report.csv");

        List<Map<String, Object>> reportData = adminService.getServiceReport();
        PrintWriter writer = response.getWriter();
        writer.println("Service Name,Total Revenue,Booking Count");

        for (Map<String, Object> row : reportData) {
            writer.println(
                    row.get("booking_Service_name") + "," +
                            row.get("totalRevenue") + "," +
                            row.get("bookingCount")
            );
        }
    }

    @GetMapping("/category-bookings")
    public ResponseEntity<List<Map<String, Object>>> getCategoryBookings(
            @RequestParam(value = "category", required = false) String categoryFilter,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {

        try {
            List<Map<String, Object>> bookings =
                    adminService.getCategoryBookings(categoryFilter, startDate, endDate);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
