package com.kushi.in.app.controller;


import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.model.*;
import com.kushi.in.app.service.AdminService;
import com.kushi.in.app.service.BookingService;

import com.kushi.in.app.service.CustomerService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kushi.in.app.config.AppConstants.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"https://kushiservices.com","https://www.kushiservices.com"}) // {KUSHI_SERVICES_URL, KUSHI_SERVICES_WWW_URL}) // Update with actual frontend URL for production
public class AdminController {

    private AdminService adminService;
    private final CustomerService customerService;

    private BookingService bookingService;


    // Constructor injection is preferred for better testability and immutability
    public AdminController(AdminService adminService,
                           CustomerService customerService,

                           BookingService bookingService) {
        this.adminService = adminService;
        this.customerService = customerService;

        this.bookingService = bookingService;
    }

    // =======================
    // 📌 Booking Management
    // =======================

    @GetMapping("/all-bookings")
    public List<Customer> getAllBookings() {
        // Returns all booking records from the database
        return adminService.getAllBookings();
    }

    @PostMapping("/new-booking")
    public Customer createBooking(@RequestBody Customer customer) {
        // Saves a new booking record to the database
        return adminService.saveBooking(customer);
    }

    /// Assign workers
    @PutMapping("/{id}/assign-worker")
    public ResponseEntity<String> assignWorker(@PathVariable("id") Long bookingId,
                                               @RequestBody Map<String, String> body) {
        String workername = body.get("workername");
        adminService.assignWorker(bookingId, workername);
        return ResponseEntity.ok("Worker assigned successfully");
    }


    // =======================
    // 📌 Booking Stats
    // =======================

    @GetMapping("/statistics")

    public ResponseEntity<Map<String , Object>> getbookingStatistics(
            @RequestParam(value="timePeriod",defaultValue = "all-time") String timePeriod){
        try{
            Map<String,Object> status = adminService.getbookingStatistics(timePeriod);// Call the service to get statistics based on the timePeriod
            return ResponseEntity.ok(status); // Return the statistics with HTTP 200 OK status
        }catch (Exception e){
            e.printStackTrace(); // Print the error in case something goes wrong
            return ResponseEntity.status(500).body(null);  // Return HTTP 500 Internal Server Error with no body
        }

    }

    @GetMapping("/financial-statistics")
    public ResponseEntity<Map<String, Object>> getFinancialStatistics(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        try {
            return ResponseEntity.ok(
                    adminService.getFinancialStatistics(filter, startDate, endDate)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getbookingOverview(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        return ResponseEntity.ok(adminService.getOverview(filter, startDate, endDate));
    }


    @GetMapping("/dashboard-overview")
    public ResponseEntity<Map<String, Object>> getDashboardOverview() {
        return ResponseEntity.ok(adminService.getOverview("all-time", null, null));
    }





    @GetMapping("/today-bookings")
    public ResponseEntity<Long> getTodayBookings() {
        long count = adminService.getTodayBookings();
        return ResponseEntity.ok(count);
    }


    @GetMapping(value = "/todays-schedule", produces = "application/json")
    public ResponseEntity<List<BookingDTO>> getTodaysBookings() {
        return ResponseEntity.ok(bookingService.getTodaysBookings());
    }


    @GetMapping("/pending-approvals")
    public ResponseEntity<Long> getPendingApprovals() {
        long count = adminService.getPendingApprovals();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/recent-bookings")
    public ResponseEntity<List<Customer>> getRecentBookingsByDate() {
        List<Customer> recentBookings = adminService.getRecentBookingsByDate();
        return ResponseEntity.ok(recentBookings);
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
    public ResponseEntity<List<Map<String, Object>>> getRevenueByService(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        return ResponseEntity.ok(adminService.getRevenueByService(filter, startDate, endDate));
    }



// =======================
    // 📌 Top Customers & Services
    // =======================


    //Top booking customers
    @GetMapping("/top-booked-customers")
    public ResponseEntity<List<CustomerDTO>> getTopBookedCustomers() {
        return ResponseEntity.ok(adminService.getTopBookedCustomers());
    }

    //Top Services
    @GetMapping("/top-services")
    public ResponseEntity<Map<String, Object>> getTopServices() {
        Map<String, Object> response = new HashMap<>();
        response.put("topServices", adminService.getTopServices());
        return ResponseEntity.ok(response);
    }


    // To fetch ratings

    @GetMapping("/top-rated-services")
    public ResponseEntity<List<ServiceDTO>> getTopRatedServices() {
        return ResponseEntity.ok(adminService.getTopRatedServices());
    }


    //to fetch recent/ new bookings

    // =======================
    // 📌 Invoice & Reports
    // =======================

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        List<InvoiceDTO> invoices = adminService.getAllInvoices();

        // ✅ Filter only completed bookings
        List<InvoiceDTO> completedInvoices = invoices.stream()
                .filter(invoice -> "completed".equalsIgnoreCase(invoice.getBookingStatus()))
                .toList();

        if (completedInvoices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(completedInvoices);
    }


    //financial management report
    @GetMapping(value = "/service-report/csv", produces = "text/csv")
    public void downloadServiceReportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=service_report.csv");

        List<Map<String, Object>> reportData = adminService.getServiceReport();
        PrintWriter writer = response.getWriter();

        // Header
        writer.println("Service Name,Total Revenue,Booking Count");

        for (Map<String, Object> row : reportData) {
            writer.println(
                    row.get("booking_Service_name") + "," +
                            row.get("totalRevenue") + "," +
                            row.get("bookingCount")
            );
        }
    }



    @GetMapping("/recent-activities")
    public ResponseEntity<List<RecentActivityDTO>> getRecentActivities() {
        return ResponseEntity.ok(adminService.getRecentActivities());
    }


}

 
