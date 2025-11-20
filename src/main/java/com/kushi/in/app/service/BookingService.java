package com.kushi.in.app.service;


import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.model.BookingDTO;
import com.kushi.in.app.model.BookingRequest;
import com.kushi.in.app.model.OrderDTO;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Customer createBooking(BookingRequest request);

    List<BookingDTO> getAllBookings();

<<<<<<< HEAD
    Customer updateBookingStatus(Long bookingId, String status, String canceledBy, String cancellationReason);

    void sendBookingNotification(String email, String phoneNumber, String status);


    Customer updateBookingDiscount(Long id, double discount);
=======
    Customer updateBookingStatus(Long bookingId, String status, String canceledBy);

    void sendBookingNotification(String email, String phoneNumber, String status);

    void updateBookingDiscount(Long bookingId, Double discount);
>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788




<<<<<<< HEAD
    Optional<Customer> getBookingDetailsById(Long id);
=======
>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788
}
