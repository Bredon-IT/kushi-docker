package com.kushi.in.app.service.impl;

import com.kushi.in.app.service.NotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SnsException;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final SnsClient snsClient;
    private final SesClient sesClient;

    public NotificationServiceImpl(SnsClient snsClient, SesClient sesClient) {
        this.snsClient = snsClient;
        this.sesClient = sesClient;
    }

    /** 🔹 Convert phone to E.164 format (required by AWS SNS) */
    private String toE164(String phone) {
        if (phone == null) return null;
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() == 10) return "+91" + digits;   // assume India
        if (digits.startsWith("91") && digits.length() == 12) return "+" + digits;
        if (phone.startsWith("+")) return phone;
        return "+" + digits;
    }

    /** 🔹 Send SMS using AWS SNS */
    @Async
    public void sendNotification(String phone, String message) {
        try {
            String phoneE164 = toE164(phone);
            snsClient.publish(PublishRequest.builder()
                    .message(message)
                    .phoneNumber(phoneE164)
                    .build());
        } catch (SnsException e) {
            e.printStackTrace();
        }
    }

    /** 🔹 Email Disabled Completely */
    private void sendEmail(String to, String subject, String text) {
        // 🚫 EMAIL DISABLED – PRINTING ONLY
        System.out.println("Email sending DISABLED → [" + subject + "] to " + to);
    }

    /** 🔹 Booking Received */
    @Async
    @Override
    public void sendBookingReceived(String email, String phone, String customer, String service, LocalDateTime date) {
        sendEmail(email, "🎉 Booking Received",
                "Hello " + customer + ", your booking for " + service + " is received.");

        sendNotification(phone,
                "Hi " + customer + ", your booking for " + service + " is received.");

        System.out.println("sendBookingReceived() executed (EMAIL DISABLED)");
    }

    /** 🔹 Booking Confirmed */
    @Async
    @Override
    public void sendBookingConfirmation(String email, String phone, String customer, String service, LocalDateTime date) {
        sendEmail(email, "✅ Booking Confirmed",
                "Hello " + customer + ", your booking for " + service + " is confirmed.");

        sendNotification(phone,
                "Hi " + customer + ", your booking for " + service + " is confirmed.");

        System.out.println("sendBookingConfirmation() executed (EMAIL DISABLED)");
    }

    /** 🔹 Booking Declined */
    @Async
    @Override
    public void sendBookingDecline(String email, String phone, String customer, String service, LocalDateTime date) {
        sendEmail(email, "❌ Booking Declined",
                "Hello " + customer + ", your booking for " + service + " is declined.");

        sendNotification(phone,
                "Hi " + customer + ", your booking for " + service + " is declined.");

        System.out.println("sendBookingDecline() executed (EMAIL DISABLED)");
    }

    /** 🔹 Booking Completed */
    @Async
    @Override
    public void sendBookingCompleted(String email, String phone, String customer, String service, LocalDateTime date) {
        sendEmail(email, "🎉 Booking Completed",
                "Hello " + customer + ", your booking for " + service + " is completed.");

        sendNotification(phone,
                "Hi " + customer + ", your booking for " + service + " is completed.");

        System.out.println("sendBookingCompleted() executed (EMAIL DISABLED)");
    }
}
