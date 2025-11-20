package com.kushi.in.app.service;

import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.model.CustomerDTO;
import com.kushi.in.app.model.InvoiceDTO;
import com.kushi.in.app.model.ServiceDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface AdminService {
    List<Customer> getAllBookings();
    Customer saveBooking(Customer customer);
  ///assign worker
    void assignWorker(Long bookingId,String workerName);// Declares a method to assign a worker to a booking using its ID
    Map<String, Object> getbookingStatistics(String timePeriod);
    Map<String, Object> getOverview(String timePeriod);
    List<String> getVisitStatuses();
    List<String> updateVisitStatuses();
    List<Customer> getRecentBookingsByDate();
    List<Map<String, Object>> getRevenueByService();
    long getTodayBookings();
    long getPendingApprovals();
    List<CustomerDTO> getTopBookedCustomers();
    List<Map<String, Object>> getTopServices();
    List<ServiceDTO> getTopRatedServices();
    List<InvoiceDTO> getAllInvoices();
    List<Map<String, Object>> getServiceReport();

    // ✅ Add this line for category bookings
    List<Map<String, Object>> getCategoryBookings(String categoryFilter, String startDate, String endDate);
}
