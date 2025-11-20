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

    /** 🔹 Send Email using AWS SES */
    private void sendEmail(String to, String subject, String text) {
        try {
            Destination destination = Destination.builder()
                    .toAddresses(to)
                    .build();

            Message message = Message.builder()
                    .subject(Content.builder().data(subject).build())
                    .body(Body.builder().text(Content.builder().data(text).build()).build())
                    .build();

            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .source("meenugalikhitha2002@gmail.com") // ✅ must be verified in SES
                    .destination(destination)
                    .message(message)
                    .build();

            sesClient.sendEmail(emailRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 🔹 Booking Received (New) */
    @Async
    @Override
    public void sendBookingReceived(String email, String phone, String customer, String service, LocalDateTime date) {
        sendEmail(email, "🎉 Booking Received - Kushi Services",
                "Hello " + customer + ",\nYour booking for " + service + " on " + date + " has been successfully received. We will contact you shortly to confirm.");
        sendNotification(phone, "Hi " + customer + ", your booking for " + service + " is received. We'll confirm shortly.");
    }

    /** 🔹 Booking Confirmed */
    @Async
    @Override
    public void sendBookingConfirmation(String email, String phone, String customer, String service, LocalDateTime date) {
        sendEmail(email, "✅ Booking Confirmed - Kushi Cleaning Services",
                "Hello " + customer + ",\nYour booking for " + service + " on " + date + " has been confirmed.");
        sendNotification(phone, "Hi " + customer + ", your booking for " + service + " on " + date + " is confirmed ✅");
    }

    /** 🔹 Booking Declined */
    @Async
    @Override
    public void sendBookingDecline(String email, String phone, String customer, String service, LocalDateTime date) {
        sendEmail(email, "❌ Booking Declined - Kushi Cleaning Services",
                "Hello " + customer + ",\nSorry, your booking for " + service + " on " + date + " has been declined.");
        sendNotification(phone, "Hi " + customer + ", your booking for " + service + " on " + date + " has been declined ❌");
    }

    /** 🔹 Booking Completed */
    @Async
    @Override
    public void sendBookingCompleted(String email, String phone, String customer, String service, LocalDateTime date) {
        sendEmail(email, "🎉 Booking Completed - Kushi Cleaning Services",
                "Hello " + customer + ",\nYour booking for " + service + " on " + date + " is successfully completed.");
        sendNotification(phone, "Hi " + customer + ", your booking for " + service + " is completed 🎉");
    }
}

