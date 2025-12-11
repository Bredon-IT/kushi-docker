package com.kushi.in.app.service;

import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface AdminService {
  List<Customer> getAllBookings();
  Customer saveBooking(Customer customer);
  ///assign worker
  void assignWorker(Long bookingId,String workerName);// Declares a method to assign a worker to a booking using its ID

  List<String> getVisitStatuses();
  List<String> updateVisitStatuses();
  List<Customer> getRecentBookingsByDate();

  long getTodayBookings();
  long getPendingApprovals();
  List<CustomerDTO> getTopBookedCustomers();
  List<Map<String, Object>> getTopServices();
  List<ServiceDTO> getTopRatedServices();
  List<InvoiceDTO> getAllInvoices();
  List<Map<String, Object>> getServiceReport();


  List<RecentActivityDTO> getRecentActivities();

  List<Map<String, Object>> getRevenueByService(String filter, String startDate, String endDate);

  Map<String, Object> getbookingStatistics(String timePeriod);


  Map<String, Object> getOverview(String filter, String startDate, String endDate);

  Map<String, Object> getFinancialStatistics(String filter, String startDate, String endDate);

  void removeWorker(Long bookingId, String workername);

    List<BookingDTO> getTodaysBookings();
}
