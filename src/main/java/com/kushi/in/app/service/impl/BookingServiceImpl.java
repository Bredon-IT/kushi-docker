package com.kushi.in.app.service.impl;

import com.kushi.in.app.dao.CustomerRepository;
import com.kushi.in.app.model.*;
import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.dao.BookingRepository;
import com.kushi.in.app.service.BookingService;
import com.kushi.in.app.service.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private JavaMailSender mailSender;

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final NotificationService notificationService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              NotificationService notificationService,
                              CustomerRepository customerRepository) {
        this.bookingRepository = bookingRepository;
        this.notificationService = notificationService;
        this.customerRepository = customerRepository;
    }

    @Override

    public Customer createBooking(BookingRequest request) {
        Customer booking = new Customer();

        booking.setCustomer_id(request.getCustomerId());
        booking.setCustomer_name(request.getCustomerName());
        booking.setCustomer_email(request.getCustomerEmail());
        booking.setCustomer_number(request.getCustomerNumber());
        booking.setAddress_line_1(request.getAddressLine1());
        booking.setAddress_line_2(request.getAddressLine2());
        booking.setAddress_line_3(request.getAddressLine3());
        booking.setCity(request.getCity());
        booking.setZip_code(request.getZipCode());
        booking.setBooking_amount(request.getBookingAmount());
        booking.setTotalAmount(request.getTotalAmount());
        booking.setBooking_service_name(request.getBookingServiceName());
        booking.setRemarks(request.getRemarks());
        booking.setBookingStatus(request.getBookingStatus() != null ? request.getBookingStatus() : "Pending");
         booking.setCreated_by(request.getCreatedBy() != null ? request.getCreatedBy() : "Customer");
        booking.setCreated_date(LocalDateTime.now().toString());
        booking.setBooking_time(request.getBookingTime());
        booking.setSite_visit(request.getSite_visit() != null ? request.getSite_visit(): "Pending");
        // PAYMENT METHOD + STATUS
        booking.setPaymentMethod(request.getPaymentMethod());
        booking.setPaymentStatus(
                request.getPaymentStatus() != null ? request.getPaymentStatus() : "Unpaid"
        );
        // Parse bookingDate
        if (request.getBookingDate() != null && !request.getBookingDate().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            booking.setBookingDate(LocalDateTime.parse(request.getBookingDate()));
        }

        // Save the booking first to get the booking ID
        Customer savedBooking = bookingRepository.save(booking);

        // Send a notification that the booking has been successfully created
        try {
            notificationService.sendBookingReceived(
                    savedBooking.getCustomer_email(),
                    savedBooking.getCustomer_number(),
                    savedBooking.getCustomer_name(),
                    savedBooking.getBooking_service_name(),
                    savedBooking.getBookingDate()
            );
        } catch (Exception e) {
            e.printStackTrace();
            // You can also log this error more formally
        }

        return savedBooking;
    }


    @Override
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream().map(c -> {
            BookingDTO dto = new BookingDTO();
            dto.setBooking_id(c.getBooking_id());
            dto.setAddress_line_1(c.getAddress_line_1());
            dto.setAddress_line_2(c.getAddress_line_2());
            dto.setAddress_line_3(c.getAddress_line_3());
            // map to booking_id
            dto.setCustomer_name(c.getCustomer_name());
            dto.setCustomer_email(c.getCustomer_email());
            dto.setCustomer_number(c.getCustomer_number());
            dto.setBooking_service_name(c.getBooking_service_name());
            dto.setBooking_amount(c.getBooking_amount());
            dto.setBooking_date(c.getBookingDate() != null ? c.getBookingDate().toString() : null);
            dto.setBooking_time(c.getBooking_time());
            dto.setBookingStatus(c.getBookingStatus());
            dto.setPaymentStatus(c.getPaymentStatus());
            dto.setCity(c.getCity());
            dto.setDiscount(c.getDiscount());
            dto.setGrand_total(c.getGrand_total());
            dto.setCancellation_reason(c.getCancellation_reason());
            dto.setPaymentMethod(c.getPaymentMethod());

            dto.setSite_visit(c.getSite_visit());
            dto.setAddress(c.getAddress_line_1());
            // FIX: Convert comma-separated String to List<String>
            dto.setWorker_assign(
                    c.getWorker_assign() != null && !c.getWorker_assign().isEmpty()
                            ? Arrays.asList(c.getWorker_assign().split(","))
                            : Collections.emptyList()
            );  dto.setCanceledBy(c.getCanceledBy());
            return dto;
        }).toList();
    }

    @Override
    public Customer updateBookingStatus(Long bookingId, String status, String canceledBy, String cancellationReason) {
        Customer customer = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        customer.setBookingStatus(status != null ? status.toLowerCase() : "pending");

        if ("cancelled".equalsIgnoreCase(status)) {
            customer.setCanceledBy(canceledBy);
            customer.setCancellation_reason(cancellationReason); // ← SAVE reason
        }
        bookingRepository.save(customer);

        try {
            if ("confirmed".equalsIgnoreCase(status)) {
                notificationService.sendBookingConfirmation(
                        customer.getCustomer_email(),
                        customer.getCustomer_number(),
                        customer.getCustomer_name(),
                        customer.getBooking_service_name(),
                        customer.getBookingDate());

            } else if ("cancelled".equalsIgnoreCase(status)) {
                notificationService.sendBookingDecline(
                        customer.getCustomer_email(),
                        customer.getCustomer_number(),
                        customer.getCustomer_name(),
                        customer.getBooking_service_name(),
                        customer.getBookingDate());
                        customer.getCancellation_reason();

            } else if ("completed".equalsIgnoreCase(status)) {
                notificationService.sendBookingCompleted(
                        customer.getCustomer_email(),
                        customer.getCustomer_number(),
                        customer.getCustomer_name(),
                        customer.getBooking_service_name(),
                        customer.getBookingDate());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customer;
    }


    @Override
    public void sendBookingNotification(String email, String phoneNumber, String status) {
        System.out.println("Sending notification for status: " + status +
                " to email: " + email + " and phone: " + phoneNumber);
        // TODO: Implement real notification logic
    }


    @Override
    public Customer updateBookingDiscount(Long bookingId, double discount) {
        Customer booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        double baseAmount = booking.getBooking_amount() != null ? booking.getBooking_amount() : 0.0;
        double validDiscount = Math.min(Math.max(discount, 0.0), baseAmount);
        double grandTotal = baseAmount - validDiscount;

        booking.setDiscount(validDiscount);
        booking.setGrand_total(grandTotal);

        return bookingRepository.save(booking);
    }




    @Override
    public Optional<Customer> getBookingDetailsById(Long id) {
        // Simple call to the JpaRepository method
        return bookingRepository.findById(id);
    }




    @Override
    @Transactional
    public Customer updateBookingInspection(Long id, InspectionUpdateRequest request) {

        Customer booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        // Update Inspection Values
        booking.setInspection_status(request.getInspection_status());
        booking.setSite_visit(request.getSite_visit());
        booking.setWorker_assign(request.getWorker_assign());

        // ⭐ Update Final Amount
        booking.setBooking_amount(request.getBooking_amount());
        booking.setDiscount(request.getDiscount());

        double finalTotal = request.getBooking_amount() - request.getDiscount();
        if (finalTotal < 0) finalTotal = 0;

        booking.setGrand_total(finalTotal);

        // ⭐ Update Booking Status
        booking.setBookingStatus(request.getBookingStatus());

        return bookingRepository.save(booking);
    }



    @Override
    public List<Customer> getBookingsByInspectionStatus(String status) {
        return bookingRepository.findByInspectionStatus(status);
    }


    public List<BookingDTO> getTodaysBookings() {
        LocalDate today = LocalDate.now();

        List<Customer> bookings = bookingRepository.findBookingsByDate(today);

        return bookings.stream()
                .filter(b -> !"completed".equalsIgnoreCase(b.getBookingStatus()))
                .map(c -> {
                    BookingDTO dto = new BookingDTO();
                    dto.setBooking_id(c.getBooking_id());
                    dto.setCustomer_name(c.getCustomer_name());
                    dto.setBooking_service_name(c.getBooking_service_name());
                    dto.setAddress_line_1(c.getAddress_line_1());
                    dto.setBookingStatus(c.getBookingStatus());
                    dto.setBooking_date(
                            c.getBookingDate() != null ? c.getBookingDate().toString() : null
                    );
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Customer updatePaymentStatus(Long bookingId, String paymentStatus) {

        Customer booking = customerRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setPaymentStatus(paymentStatus);

        return customerRepository.save(booking);
    }



}
