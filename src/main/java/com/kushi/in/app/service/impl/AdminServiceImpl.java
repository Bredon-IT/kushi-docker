package com.kushi.in.app.service.impl;

import com.kushi.in.app.dao.AdminRepository;


import com.kushi.in.app.dao.CustomerRepository;
import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.entity.Services;
import com.kushi.in.app.model.CustomerDTO;
import com.kushi.in.app.model.InvoiceDTO;
import com.kushi.in.app.model.RecentActivityDTO;
import com.kushi.in.app.model.ServiceDTO;
import com.kushi.in.app.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private final CustomerRepository customerRepository;

    public AdminServiceImpl(AdminRepository adminRepository, CustomerRepository customerRepository) {
        this.adminRepository = adminRepository;
        this.customerRepository = customerRepository;
    }


    @Override
    public List<Customer> getAllBookings() {
        // Retrieves all booking records from the database
        return adminRepository.findAll();
    }


    // Saves a new booking to the database
    @Override
    public Customer saveBooking(Customer customer) {
        return adminRepository.save(customer);
    }

    // Assigns a worker to an existing booking based on the booking ID
    @Override
    public void assignWorker(Long bookingId, String workerName) {
        Customer booking = adminRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        String existing = booking.getWorker_assign(); // assume it's a comma-separated string
        if (existing == null || existing.trim().isEmpty()) {
            booking.setWorker_assign(workerName);
        } else {
            booking.setWorker_assign(existing + "," + workerName);
        }

        adminRepository.save(booking); // this **persists in DB**
    }


    @Override
    public Map<String, Object> getbookingStatistics(String timePeriod) {
        List<Customer> bookings = adminRepository.findAll();
        LocalDateTime now = LocalDateTime.now();  // use LocalDateTime since entity has LocalDateTime

        bookings = bookings.stream()
                .filter(b -> Optional.ofNullable(b.getBookingDate())
                        .map(date -> {
                            switch (timePeriod.toLowerCase()) {
                                case "one-week":
                                    return date.isAfter(now.minusWeeks(1));
                                case "two-weeks":
                                    return date.isAfter(now.minusWeeks(2));
                                case "one-month":
                                    return date.isAfter(now.minusMonths(1));
                                default:
                                    return true;
                            }
                        })
                        .orElse(false))
                .collect(Collectors.toList());

        Map<String, Double> serviceRevenue = new HashMap<>();
        double totalAmount = 0.0;

        for (Customer booking : bookings) {
            String service = booking.getBooking_service_name();
            // ✅ Safe null handling
            double amount = booking.getTotalAmount() != null ? booking.getTotalAmount() : 0.0;
            totalAmount += amount;
            serviceRevenue.put(service, serviceRevenue.getOrDefault(service, 0.0) + amount);
        }

        // ✅ Booking Trends by Date
        Map<LocalDate, Long> bookingTrends = bookings.stream()
                .filter(b -> b.getBookingDate() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getBookingDate().toLocalDate(), // convert LocalDateTime → LocalDate
                        TreeMap::new,
                        Collectors.counting()
                ));

        Map<String, Object> response = new HashMap<>();
        response.put("labels", new ArrayList<>(serviceRevenue.keySet()));
        response.put("data", new ArrayList<>(serviceRevenue.values()));
        response.put("totalcustomers", bookings.size());
        response.put("totalbooking_amount", totalAmount);
        response.put("bookingTrends", bookingTrends);

        return response;
    }

    @Override
    public Map<String, Object> getFinancialStatistics(String filter, String startDate, String endDate) {

        List<Customer> bookings = adminRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;
        LocalDateTime end = now;

        // ===========================
        //      FILTER LOGIC
        // ===========================
        if (filter != null) {
            switch (filter.toLowerCase()) {

                case "today":
                    start = now.toLocalDate().atStartOfDay();
                    break;

                case "month":
                    start = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
                    break;

                case "year":
                    start = now.toLocalDate().withDayOfYear(1).atStartOfDay();
                    break;


                case "custom":
                    if (startDate != null && endDate != null) {
                        start = LocalDate.parse(startDate).atStartOfDay();
                        end = LocalDate.parse(endDate).atTime(23, 59, 59);
                    } else {
                        start = LocalDate.of(2000,1,1).atStartOfDay();
                    }
                    break;

                default:
                    start = LocalDate.of(2000,1,1).atStartOfDay();
            }
        } else {
            if (startDate != null && endDate != null) {
                start = LocalDate.parse(startDate).atStartOfDay();
                end = LocalDate.parse(endDate).atTime(23,59,59);
            } else {
                start = LocalDate.of(2000,1,1).atStartOfDay();
            }
        }

        LocalDateTime finalStart = start;
        LocalDateTime finalEnd = end;

        // APPLY FILTER
        List<Customer> filtered = bookings.stream()
                .filter(b -> b.getBookingDate() != null &&
                        !b.getBookingDate().isBefore(finalStart) &&
                        !b.getBookingDate().isAfter(finalEnd))
                .collect(Collectors.toList());

        // ===========================
        //      CALCULATE STATS
        // ===========================
        double totalAmount = filtered.stream()
                .mapToDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount() : 0.0)
                .sum();

        int totalCustomers = (int) filtered.stream()
                .map(Customer::getCustomer_id)
                .distinct()
                .count();

        // Revenue by Service
        Map<String, Double> revenueByService = new LinkedHashMap<>();
        for (Customer b : filtered) {
            String service = b.getBooking_service_name();
            double amount = (b.getTotalAmount() != null) ? b.getTotalAmount() : 0.0;

            revenueByService.put(service,
                    revenueByService.getOrDefault(service, 0.0) + amount);
        }

        // Booking Trends (per date)
        Map<LocalDate, Long> bookingTrends = filtered.stream()
                .filter(b -> b.getBookingDate() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getBookingDate().toLocalDate(),
                        TreeMap::new,
                        Collectors.counting()
                ));

        Map<String, Object> result = new HashMap<>();
        result.put("labels", revenueByService.keySet());
        result.put("data", revenueByService.values());
        result.put("totalbooking_amount", totalAmount);
        result.put("totalcustomers", totalCustomers);
        result.put("bookingTrends", bookingTrends);

        return result;
    }



    @Override
    public Map<String, Object> getOverview(String filter, String startDate, String endDate) {

        List<Customer> bookings = adminRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;
        LocalDateTime end = now;

        // ===========================
        //       FILTER LOGIC
        // ===========================
        if (filter != null) {
            switch (filter.toLowerCase()) {

                case "today":
                    start = now.toLocalDate().atStartOfDay();
                    break;

                case "month":
                    start = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                    break;

                case "year":
                    start = now.withDayOfYear(1).toLocalDate().atStartOfDay();
                    break;

                case "custom":
                    if (startDate != null && endDate != null) {
                        start = LocalDate.parse(startDate).atStartOfDay();
                        end = LocalDate.parse(endDate).atTime(23, 59, 59);
                    } else {
                        start = LocalDate.of(2000, 1, 1).atStartOfDay();
                    }
                    break;

                default:
                    start = LocalDate.of(2000, 1, 1).atStartOfDay();
            }

        } else {
            // no filter → custom but filter missing
            if (startDate != null && endDate != null) {
                start = LocalDate.parse(startDate).atStartOfDay();
                end = LocalDate.parse(endDate).atTime(23, 59, 59);
            } else {
                start = LocalDate.of(2000, 1, 1).atStartOfDay();
            }
        }

        // ===========================
        //     APPLY FILTER
        // ===========================
        LocalDateTime finalStart = start;
        LocalDateTime finalEnd = end;

        List<Customer> filteredBookings = bookings.stream()
                .filter(b -> b.getBookingDate() != null &&
                        !b.getBookingDate().isBefore(finalStart) &&
                        !b.getBookingDate().isAfter(finalEnd))
                .collect(Collectors.toList());

        // ===========================
        //     CALCULATE VALUES
        // ===========================
        double totalAmount = filteredBookings.stream()
                .mapToDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount() : 0.0)
                .sum();

        int totalCustomers = (int) filteredBookings.stream()
                .map(Customer::getCustomer_id)
                .distinct()
                .count();

        int totalBookings = filteredBookings.size();

        // monthlyIncome should be based on filtered range, not current month
        double monthlyIncome = totalAmount;

        double totalExpenses = filteredBookings.stream()
                .mapToDouble(b -> b.getServices() != null ? b.getServices().getService_cost() : 0.0)
                .sum();

        double netProfit = totalAmount - totalExpenses;

        // ===========================
        //     BUILD RESPONSE
        // ===========================
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalAmount", totalAmount);
        overview.put("totalCustomers", totalCustomers);
        overview.put("totalBookings", totalBookings);
        overview.put("monthlyIncome", monthlyIncome);
        overview.put("netProfit", netProfit);

        return overview;
    }





    @Override
    public List<Customer> getRecentBookingsByDate() {
        return adminRepository.findAll().stream()
                .filter(customer -> customer.getBookingDate() != null)
                .sorted(Comparator.comparing(Customer::getBookingDate).reversed())
                .limit(5) // Optional: Limit to recent 10 bookings
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getVisitStatuses() {
        return adminRepository.findAll().stream()
                .map(customer -> {

                    if ("Completed".equalsIgnoreCase(customer.getBookingStatus())) {
                        return "visit completed";
                    } else {
                        return "visit not completed";
                    }


                }).collect(Collectors.toList());
    }

    @Override
    public List<String> updateVisitStatuses() {
        return adminRepository.findAll().stream()
                .map(customer -> {
                    String status;
                    if ("Completed".equalsIgnoreCase(customer.getBookingStatus())) {
                        status = "visit completed";
                    } else {
                        status = "visit not completed";
                    }

                    customer.setSite_visit(status);
                    adminRepository.save(customer);
                    return status;

                }).collect(Collectors.toList());
    }


    @Override
    public List<Map<String, Object>> getRevenueByService(String filter, String startDate, String endDate) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;
        LocalDateTime endTime = now;

        if (filter != null) {
            switch (filter.toLowerCase()) {
                case "today":
                    start = now.toLocalDate().atStartOfDay();
                    break;
                case "month":
                    start = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                    break;
                case "year":
                    start = now.withDayOfYear(1).toLocalDate().atStartOfDay();
                    break;
                default:
                    start = LocalDate.of(2000, 1, 1).atStartOfDay();
            }
        }
        else if (startDate != null && endDate != null) {
            start = LocalDate.parse(startDate).atStartOfDay();
            endTime = LocalDate.parse(endDate).atTime(23, 59, 59);
        }
        else {
            start = LocalDate.of(2000, 1, 1).atStartOfDay();
        }

        // ====== APPLY DATE FILTER IN JAVA ======
        List<Customer> bookings = adminRepository.findAll();

        LocalDateTime finalStart = start;
        LocalDateTime finalEnd = endTime;

        List<Customer> filtered = bookings.stream()
                .filter(b -> b.getBookingDate() != null &&
                        !b.getBookingDate().isBefore(finalStart) &&
                        !b.getBookingDate().isAfter(finalEnd))
                .collect(Collectors.toList());

        // ====== GROUP BY SERVICE AND CALCULATE REVENUE ======
        Map<String, Double> result = new HashMap<>();

        for (Customer b : filtered) {
            String service = b.getBooking_service_name();
            double amount = b.getTotalAmount() != null ? b.getTotalAmount() : 0.0;

            result.put(service, result.getOrDefault(service, 0.0) + amount);
        }

        // Convert to List<Map<String, Object>> (your frontend expects this)
        List<Map<String, Object>> response = new ArrayList<>();

        for (String service : result.keySet()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("serviceName", service);
            entry.put("totalRevenue", result.get(service));
            response.add(entry);
        }

        return response;
    }

    @Override
    public long getTodayBookings() {
        return adminRepository.countTodayBookings();
    }

    @Override
    public long getPendingApprovals() {
        return adminRepository.countPendingApprovals();
    }


    @Override
    public List<CustomerDTO> getTopBookedCustomers() {
        List<Object[]> results = adminRepository.findTopBookedCustomers();

        return results.stream().map(obj -> {
            CustomerDTO dto = new CustomerDTO();

            dto.setCustomer_email((String) obj[0]);         // customer_email
            dto.setCustomer_name((String) obj[1]);          // customer_name
            dto.setBooking_amount(((Number) obj[2]).doubleValue()); // booking_count (optional field)
            dto.setAddress_line_1((String) obj[3]);
            dto.setCustomer_number((String) obj[4]);
            dto.setTotalAmount(((Number) obj[5]).doubleValue());

            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    public List<Map<String, Object>> getTopServices() {
        List<Object[]> results = adminRepository.findTopServices(PageRequest.of(0, 10));
        List<Map<String, Object>> responseList = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> servicesData = new HashMap<>();
            servicesData.put("booking_service_name", row[0]); // service_name
            servicesData.put("service_image_url", row[1]);
            servicesData.put("service_description", row[2]);
            servicesData.put("bookingCount", ((Number) row[3]).longValue());
            servicesData.put("service_name", row[4]);
            responseList.add(servicesData);
        }

        return responseList;
    }


    @Override
    public List<ServiceDTO> getTopRatedServices() {
        List<Object[]> results = adminRepository.findTopRatedServices();
        return results.stream()
                .map(obj -> new ServiceDTO(
                        (String) obj[0],
                        ((Number) obj[1]).doubleValue(),
                        ((Number) obj[2]).longValue(),
                        (String) obj[3],
                        ((Number) obj[4]).doubleValue(),
                        (String) obj[5]
                ))
                .collect(Collectors.toList());
    }


    @Override
    public List<InvoiceDTO> getAllInvoices() {
        // Fetch customers ordered by booking date
        List<Customer> customers = customerRepository.findAllByOrderByBookingDateDesc();

        return customers.stream()
                .filter(Objects::nonNull)
                .map(customer -> {
                    InvoiceDTO dto = new InvoiceDTO();

                    // Booking Details
                    dto.setBooking_id(customer.getBooking_id());
                    dto.setBookingDate(customer.getBookingDate());
                    dto.setBooking_amount(customer.getBooking_amount() != null ? customer.getBooking_amount() : 0.0);
                    dto.setTotal_amount(customer.getTotalAmount() != null ? customer.getTotalAmount() : 0.0);
                    dto.setWorker_assign(customer.getWorker_assign());
                    dto.setCity(customer.getCity());
                    dto.setBooking_service_name(customer.getBooking_service_name());
                    dto.setDiscount(customer.getDiscount());
                    dto.setGrand_total(customer.getGrand_total());
                    dto.setPayment_method(customer.getPaymentMethod());
                    dto.setPayment_status(customer.getPaymentStatus());

                    // ✅ FIX: Map booking status
                    dto.setBookingStatus(customer.getBookingStatus());

                    // Customer Details
                    dto.setCustomer_id(customer.getCustomer_id());
                    dto.setCustomer_name(customer.getCustomer_name());
                    dto.setCustomer_email(customer.getCustomer_email());
                    dto.setCustomer_number(customer.getCustomer_number());
                    dto.setAddress_line_1(customer.getAddress_line_1());

                    // Service Details
                    dto.setService_id(customer.getService_id());
                    Services service = customer.getServices();
                    if (service != null) {
                        dto.setService_name(service.getService_name());
                        dto.setService_type(service.getService_type());
                        dto.setService_cost(service.getService_cost());
                        dto.setService_details(service.getService_details());
                        dto.setService_description(service.getService_description());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }


    public List<Map<String, Object>> getServiceReport() {
        List<Customer> bookings = adminRepository.findAll();

        Map<String, List<Customer>> groupedBookings = bookings.stream()
                .filter(customer -> customer.getBooking_service_name() != null)
                .collect(Collectors.groupingBy(Customer::getBooking_service_name, LinkedHashMap::new, Collectors.toList()));

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, List<Customer>> entry : groupedBookings.entrySet()) {
            String booking_service_name = entry.getKey();
            List<Customer> serviceBookings = entry.getValue();

            double totalRevenue = serviceBookings.stream()
                    .mapToDouble(Customer::getTotalAmount)
                    .sum();

            int bookingCount = serviceBookings.size();
            Map<String, Object> map = new HashMap<>();
            map.put("booking_Service_name", booking_service_name);
            map.put("totalRevenue", totalRevenue);
            map.put("bookingCount", bookingCount);

            result.add(map);
        }
        return result;
    }

    @Override
    public List<RecentActivityDTO> getRecentActivities() {
        return adminRepository.getRecentActivities().stream().map(c -> {
            RecentActivityDTO dto = new RecentActivityDTO();
            dto.setBooking_id(c.getBooking_id());
            dto.setCustomer_name(c.getCustomer_name());
            dto.setBookingStatus(c.getBookingStatus());
            dto.setBooking_service_name(c.getBooking_service_name());
            dto.setBookingDate(c.getBookingDate());
            return dto;
        }).collect(Collectors.toList());
    }


}
