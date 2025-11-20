package com.kushi.in.app.service.impl;

import com.kushi.in.app.dao.CustomerRepository;
import com.kushi.in.app.model.BookingDTO;
import com.kushi.in.app.model.BookingRequest;
import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.dao.BookingRepository;
import com.kushi.in.app.model.OrderDTO;
import com.kushi.in.app.service.BookingService;
import com.kushi.in.app.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
<<<<<<< HEAD
import java.util.Optional;
=======
>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788
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
        booking.setPayment_status(request.getPaymentStatus() != null ? request.getPaymentStatus() : "Unpaid");
        booking.setCreated_by(request.getCreatedBy() != null ? request.getCreatedBy() : "Customer");
        booking.setCreated_date(LocalDateTime.now().toString());
        booking.setBooking_time(request.getBookingTime());
        booking.setSite_visit(request.getSite_visit() != null ? request.getSite_visit(): "Pending");
        booking.setPayment_method(request.getPayment_method());
        booking.setPayment_method(request.getPayment_method());
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

            // map to booking_id
            dto.setCustomer_name(c.getCustomer_name());
            dto.setCustomer_email(c.getCustomer_email());
            dto.setCustomer_number(c.getCustomer_number());
            dto.setBooking_service_name(c.getBooking_service_name());
            dto.setBooking_amount(c.getBooking_amount());
            dto.setBooking_date(c.getBookingDate() != null ? c.getBookingDate().toString() : null);
            dto.setBooking_time(c.getBooking_time());
            dto.setBookingStatus(c.getBookingStatus());
            dto.setPayment_status(c.getPayment_status());
            dto.setCity(c.getCity());
            dto.setDiscount(c.getDiscount());
            dto.setGrand_total(c.getGrand_total());
            dto.setCancellation_reason(c.getCancellation_reason());
            dto.setPayment_method(c.getPayment_method());
            dto.setPayment_status(c.getPayment_status());
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
<<<<<<< HEAD
    public Customer updateBookingStatus(Long bookingId, String status, String canceledBy, String cancellationReason) {
=======
    public Customer updateBookingStatus(Long bookingId, String status, String canceledBy) {
>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788
        Customer customer = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        customer.setBookingStatus(status != null ? status.toLowerCase() : "pending");

        if ("cancelled".equalsIgnoreCase(status)) {
            customer.setCanceledBy(canceledBy);
<<<<<<< HEAD
            customer.setCancellation_reason(cancellationReason); // ← SAVE reason
=======
>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788
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
<<<<<<< HEAD
    public Customer updateBookingDiscount(Long bookingId, double discount) {
        Customer booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        double baseAmount = booking.getBooking_amount() != null ? booking.getBooking_amount() : 0.0;
        double validDiscount = Math.min(Math.max(discount, 0.0), baseAmount);
        double grandTotal = baseAmount - validDiscount;

        booking.setDiscount(validDiscount);
        booking.setGrand_total(grandTotal);

        return bookingRepository.save(booking);
=======
    public void updateBookingDiscount(Long bookingId, Double discount) {
        Customer booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Save discount
        booking.setDiscount(discount);

        // Calculate and save grand total
        double finalAmount = booking.getBooking_amount() - discount;
        if (finalAmount < 0) {
            finalAmount = 0; // Prevent negative totals
        }
        booking.setGrand_total(finalAmount);

        bookingRepository.save(booking);
>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788
    }




<<<<<<< HEAD
    @Override
    public Optional<Customer> getBookingDetailsById(Long id) {
        // Simple call to the JpaRepository method
        return bookingRepository.findById(id);
    }
=======
>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788

}
