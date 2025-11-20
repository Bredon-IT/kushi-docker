package com.kushi.in.app.service;


import java.time.LocalDateTime;

public interface NotificationService {
    void sendBookingConfirmation(String email, String phone, String customerName, String service, LocalDateTime bookingDate);
    void sendBookingDecline(String email, String phone, String customerName, String service, LocalDateTime bookingDate);
    void sendBookingCompleted(String email, String phone, String customerName, String service, LocalDateTime bookingDate);
    void sendNotification(String phoneNumber, String message);
    void sendBookingReceived(String email, String phone, String customer, String service, LocalDateTime date);

}