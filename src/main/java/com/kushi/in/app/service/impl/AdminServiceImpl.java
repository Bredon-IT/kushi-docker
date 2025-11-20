package com.kushi.in.app.service.impl;

import com.kushi.in.app.dao.AdminRepository;
import com.kushi.in.app.dao.CustomerRepository;
import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.entity.Services;
import com.kushi.in.app.model.CustomerDTO;
import com.kushi.in.app.model.InvoiceDTO;
import com.kushi.in.app.model.ServiceDTO;
import com.kushi.in.app.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository, CustomerRepository customerRepository) {
        this.adminRepository = adminRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> getAllBookings() {
        return adminRepository.findAll();
    }

    @Override
    public Customer saveBooking(Customer customer) {
        return adminRepository.save(customer);
    }

    @Override
    public void assignWorker(Long bookingId, String workerName) {
        Customer booking = adminRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        String existing = booking.getWorker_assign();
        if (existing == null || existing.trim().isEmpty()) {
            booking.setWorker_assign(workerName);
        } else {
            booking.setWorker_assign(existing + "," + workerName);
        }
        adminRepository.save(booking);
    }

    @Override
    public Map<String, Object> getbookingStatistics(String timePeriod) {
        List<Customer> bookings = adminRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        bookings = bookings.stream()
                .filter(b -> b.getBookingDate() != null &&
                        switch (timePeriod.toLowerCase()) {
                            case "one-week" -> b.getBookingDate().isAfter(now.minusWeeks(1));
                            case "two-weeks" -> b.getBookingDate().isAfter(now.minusWeeks(2));
                            case "one-month" -> b.getBookingDate().isAfter(now.minusMonths(1));
                            default -> true;
                        })
                .collect(Collectors.toList());

        Map<String, Double> serviceRevenue = new LinkedHashMap<>();
        double totalAmount = 0.0;

        for (Customer booking : bookings) {
            double amount = booking.getTotalAmount() != null ? booking.getTotalAmount() : 0.0;
            totalAmount += amount;
            serviceRevenue.merge(booking.getBooking_service_name(), amount, Double::sum);
        }

        Map<LocalDate, Long> bookingTrends = bookings.stream()
                .filter(b -> b.getBookingDate() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getBookingDate().toLocalDate(),
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
    public Map<String, Object> getOverview(String timePeriod) {
        List<Customer> bookings = adminRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        bookings = bookings.stream()
                .filter(b -> b.getBookingDate() != null &&
                        switch (timePeriod.toLowerCase()) {
                            case "one-week" -> b.getBookingDate().isAfter(now.minusWeeks(1));
                            case "two-weeks" -> b.getBookingDate().isAfter(now.minusWeeks(2));
                            case "one-month" -> b.getBookingDate().isAfter(now.minusMonths(1));
                            default -> true;
                        })
                .collect(Collectors.toList());

        double totalAmount = bookings.stream()
                .mapToDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount() : 0.0)
                .sum();

        long totalCustomers = bookings.stream()
                .map(Customer::getCustomer_id)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        int totalBookings = bookings.size();

        double monthlyIncome = bookings.stream()
                .filter(b -> b.getBookingDate() != null &&
                        b.getBookingDate().getMonth() == now.getMonth() &&
                        b.getBookingDate().getYear() == now.getYear())
                .mapToDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount() : 0.0)
                .sum();

        double totalExpenses = bookings.stream()
                .mapToDouble(b -> b.getServices() != null ? b.getServices().getService_cost() : 0.0)
                .sum();

        double netProfit = totalAmount - totalExpenses;

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
                .filter(b -> b.getBookingDate() != null)
                .sorted(Comparator.comparing(Customer::getBookingDate).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getVisitStatuses() {
        return adminRepository.findAll().stream()
                .map(customer ->
                        "Completed".equalsIgnoreCase(customer.getBookingStatus()) ?
                                "visit completed" : "visit not completed")
                .collect(Collectors.toList());
    }

    @Override
    public List<String> updateVisitStatuses() {
        return adminRepository.findAll().stream()
                .map(customer -> {
                    String status = "Completed".equalsIgnoreCase(customer.getBookingStatus())
                            ? "visit completed" : "visit not completed";
                    customer.setSite_visit(status);
                    adminRepository.save(customer);
                    return status;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getRevenueByService() {

        List<Object[]> results = adminRepository.getRevenueByService();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("serviceName", row[0]);
            map.put("totalRevenue", row[1]);
            map.put("bookingCount", row[2]);
            response.add(map);
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
            dto.setCustomer_email((String) obj[0]);
            dto.setCustomer_name((String) obj[1]);
            dto.setBooking_amount(((Number) obj[2]).doubleValue());
            dto.setAddress_line_1((String) obj[3]);
            dto.setCustomer_number((String) obj[4]);
            dto.setTotalAmount(((Number) obj[5]).doubleValue());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getTopServices() {
        List<Object[]> results = adminRepository.findTopServices(PageRequest.of(0, 10));

        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("booking_service_name", row[0]);
            map.put("service_image_url", row[1]);
            map.put("service_description", row[2]);
            map.put("bookingCount", ((Number) row[3]).longValue());
            map.put("service_name", row[4]);
            list.add(map);
        }
        return list;
    }

    @Override
    public List<ServiceDTO> getTopRatedServices() {
        return adminRepository.findTopRatedServices().stream()
                .map(r -> new ServiceDTO(
                        (String) r[0],
                        ((Number) r[1]).doubleValue(),
                        ((Number) r[2]).longValue(),
                        (String) r[3],
                        ((Number) r[4]).doubleValue(),
                        (String) r[5]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDTO> getAllInvoices() {
        List<Customer> customers = customerRepository.findAllByOrderByBookingDateDesc();

        return customers.stream()
                .map(customer -> {
                    InvoiceDTO dto = new InvoiceDTO();

                    dto.setBooking_id(customer.getBooking_id());
                    dto.setBookingDate(customer.getBookingDate());
                    dto.setBooking_amount(customer.getBooking_amount());
                    dto.setTotalAmount(customer.getTotalAmount());
                    dto.setWorker_assign(customer.getWorker_assign());
                    dto.setCity(customer.getCity());
                    dto.setBooking_service_name(customer.getBooking_service_name());
                    dto.setDiscount(customer.getDiscount());
                    dto.setGrand_total(customer.getGrand_total());
                    dto.setPayment_method(customer.getPayment_method());
                    dto.setPayment_status(customer.getPayment_status());
                    dto.setBookingStatus(customer.getBookingStatus());

                    dto.setCustomer_id(customer.getCustomer_id());
                    dto.setCustomer_name(customer.getCustomer_name());
                    dto.setCustomer_email(customer.getCustomer_email());
                    dto.setCustomer_number(customer.getCustomer_number());
                    dto.setAddress_line_1(customer.getAddress_line_1());

                    Services s = customer.getServices();
                    if (s != null) {
                        dto.setService_id(s.getService_id());
                        dto.setService_name(s.getService_name());
                        dto.setService_type(s.getService_type());
                        dto.setService_cost(s.getService_cost());
                        dto.setService_details(s.getService_details());
                        dto.setService_description(s.getService_description());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getServiceReport() {
        List<Customer> bookings = adminRepository.findAll();

        Map<String, List<Customer>> grouped = bookings.stream()
                .filter(c -> c.getBooking_service_name() != null)
                .collect(Collectors.groupingBy(Customer::getBooking_service_name));

        List<Map<String, Object>> result = new ArrayList<>();

        for (String serviceName : grouped.keySet()) {
            List<Customer> group = grouped.get(serviceName);

            double revenue = group.stream()
                    .mapToDouble(c -> c.getTotalAmount() != null ? c.getTotalAmount() : 0.0)
                    .sum();

            Map<String, Object> map = new HashMap<>();
            map.put("booking_Service_name", serviceName);
            map.put("totalRevenue", revenue);
            map.put("bookingCount", group.size());
            result.add(map);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getCategoryBookings(String category, String startDateStr, String endDateStr) {

        LocalDateTime start = startDateStr != null && !startDateStr.isEmpty()
                ? LocalDate.parse(startDateStr).atStartOfDay()
                : null;

        LocalDateTime end = endDateStr != null && !endDateStr.isEmpty()
                ? LocalDate.parse(endDateStr).atTime(23, 59, 59)
                : null;

        List<Customer> bookings = customerRepository.findBookingsByCategoryAndDate(category, start, end);

        Map<String, Map<String, Map<String, Long>>> result = new HashMap<>();

        for (Customer c : bookings) {
            String cat = c.getServices() != null ? c.getServices().getService_category() : "Unknown";
            String sub = c.getServices() != null ? c.getServices().getService_type() : "Unknown";
            String status = Optional.ofNullable(c.getBookingStatus()).orElse("Unknown").toLowerCase();

            result
                    .computeIfAbsent(cat, k -> new HashMap<>())
                    .computeIfAbsent(sub, k -> new HashMap<>())
                    .merge(status, 1L, Long::sum);
        }

        List<Map<String, Object>> response = new ArrayList<>();

        for (String cat : result.keySet()) {
            for (String sub : result.get(cat).keySet()) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("category", cat);
                entry.put("subcategory", sub);
                Map<String, Long> st = result.get(cat).get(sub);

                entry.put("completed", st.getOrDefault("completed", 0L));
                entry.put("cancelled", st.getOrDefault("cancelled", 0L));

                response.add(entry);
            }
        }

        return response;
    }
}
