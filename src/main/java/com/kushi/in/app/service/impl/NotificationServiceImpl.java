package com.kushi.in.app.service.impl;

import com.kushi.in.app.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${aws.ses.from-email:noreply@kushiin.com}")
    private String fromEmail;

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
    @Async
    private void sendEmail(String to, String subject, String htmlBody) {
        try {
            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .destination(Destination.builder()
                            .toAddresses(to)
                            .build())
                    .message(Message.builder()
                            .subject(Content.builder()
                                    .data(subject)
                                    .charset("UTF-8")
                                    .build())
                            .body(Body.builder()
                                    .html(Content.builder()
                                            .data(htmlBody)
                                            .charset("UTF-8")
                                            .build())
                                    .build())
                            .build())
                    .source(fromEmail) // ✅ Use configured email from application.properties
                    .build();

            sesClient.sendEmail(emailRequest);
            System.out.println("✅ Email sent successfully to: " + to + " | Subject: " + subject);
        } catch (SesException e) {
            System.err.println("❌ Failed to send email to " + to + ": " + e.awsErrorDetails().errorMessage());
            e.printStackTrace();
        }
    }

    /** 🔹 Booking Received */
    @Async
    @Override
    public void sendBookingReceived(String email, String phone, String customer, String service, LocalDateTime date) {
        String htmlBody = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    <h2 style="color: #4CAF50;">🎉 Booking Received!</h2>
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Thank you for choosing Kushi! We have received your booking request.</p>
                    <div style="background-color: #f9f9f9; padding: 15px; border-left: 4px solid #4CAF50; margin: 20px 0;">
                        <p><strong>Service:</strong> %s</p>
                        <p><strong>Booking Date:</strong> %s</p>
                        <p><strong>Status:</strong> Pending Confirmation</p>
                    </div>
                    <p>Our team will review your booking and confirm shortly. You will receive a confirmation email once approved.</p>
                    <p style="margin-top: 30px;">Best regards,<br><strong>Team Kushi</strong></p>
                </div>
            </body>
            </html>
            """, customer, service, date != null ? date.toString() : "N/A");

        sendEmail(email, "🎉 Booking Received - Kushi Services", htmlBody);

        sendNotification(phone,
                "Hi " + customer + ", your booking for " + service + " is received. We'll confirm shortly!");

        System.out.println("✅ Booking received notification sent to: " + email);
    }

    /** 🔹 Booking Confirmed */
    @Async
    @Override
    public void sendBookingConfirmation(String email, String phone, String customer, String service, LocalDateTime date) {
        String htmlBody = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    <h2 style="color: #4CAF50;">✅ Booking Confirmed!</h2>
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Great news! Your booking has been confirmed.</p>
                    <div style="background-color: #e8f5e9; padding: 20px; border-radius: 8px; margin: 20px 0;">
                        <h3 style="color: #2e7d32; margin-top: 0;">Booking Details</h3>
                        <p><strong>Service:</strong> %s</p>
                        <p><strong>Scheduled Date:</strong> %s</p>
                        <p><strong>Status:</strong> <span style="color: #4CAF50; font-weight: bold;">CONFIRMED</span></p>
                    </div>
                    <p>Our team will arrive at your location on the scheduled date. Please ensure someone is available at the premises.</p>
                    <p>If you have any questions, feel free to contact our support team.</p>
                    <p style="margin-top: 30px;">Thank you for choosing Kushi!<br><strong>Team Kushi</strong></p>
                </div>
            </body>
            </html>
            """, customer, service, date != null ? date.toString() : "N/A");

        sendEmail(email, "✅ Booking Confirmed - Kushi Services", htmlBody);

        sendNotification(phone,
                "Hi " + customer + ", your booking for " + service + " is CONFIRMED for " + (date != null ? date.toLocalDate() : "soon") + "!");

        System.out.println("✅ Booking confirmation sent to: " + email);
    }

    /** 🔹 Booking Declined */
    @Async
    @Override
    public void sendBookingDecline(String email, String phone, String customer, String service, LocalDateTime date) {
        String htmlBody = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    <h2 style="color: #f44336;">Booking Update</h2>
                    <p>Dear <strong>%s</strong>,</p>
                    <p>We regret to inform you that your booking request could not be confirmed at this time.</p>
                    <div style="background-color: #ffebee; padding: 15px; border-left: 4px solid #f44336; margin: 20px 0;">
                        <p><strong>Service:</strong> %s</p>
                        <p><strong>Requested Date:</strong> %s</p>
                        <p><strong>Status:</strong> <span style="color: #f44336;">Cancelled</span></p>
                    </div>
                    <p>This may be due to service unavailability or scheduling conflicts. We apologize for any inconvenience.</p>
                    <p>You can try booking again for a different date or contact our support team for assistance.</p>
                    <p style="margin-top: 30px;">Thank you for your understanding,<br><strong>Team Kushi</strong></p>
                </div>
            </body>
            </html>
            """, customer, service, date != null ? date.toString() : "N/A");

        sendEmail(email, "Booking Update - Kushi Services", htmlBody);

        sendNotification(phone,
                "Hi " + customer + ", unfortunately your booking for " + service + " could not be confirmed. Please contact support.");

        System.out.println("✅ Booking decline notification sent to: " + email);
    }

    /** 🔹 Booking Completed */
    @Async
    @Override
    public void sendBookingCompleted(String email, String phone, String customer, String service, LocalDateTime date) {
        String htmlBody = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    <h2 style="color: #4CAF50;">🎉 Service Completed!</h2>
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Thank you for choosing Kushi! Your service has been successfully completed.</p>
                    <div style="background-color: #e8f5e9; padding: 15px; border-radius: 8px; margin: 20px 0;">
                        <p><strong>Service:</strong> %s</p>
                        <p><strong>Completed On:</strong> %s</p>
                        <p><strong>Status:</strong> <span style="color: #4CAF50; font-weight: bold;">COMPLETED</span></p>
                    </div>
                    <div style="background-color: #fff3e0; padding: 20px; border-radius: 8px; margin: 20px 0;">
                        <h3 style="margin-top: 0;">⭐ Rate Your Experience</h3>
                        <p>We'd love to hear your feedback! Please take a moment to rate our service.</p>
                        <p style="text-align: center; margin-top: 15px;">
                            <a href="https://kushiin.com/rate-service" style="background-color: #FF9800; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; font-weight: bold;">Rate Now</a>
                        </p>
                    </div>
                    <p>We hope you're satisfied with our service. If you have any concerns, please don't hesitate to reach out.</p>
                    <p style="margin-top: 30px;">Thank you for your business!<br><strong>Team Kushi</strong></p>
                </div>
            </body>
            </html>
            """, customer, service, date != null ? date.toString() : "N/A");

        sendEmail(email, "🎉 Service Completed - Rate Your Experience!", htmlBody);

        sendNotification(phone,
                "Hi " + customer + ", your service (" + service + ") is completed! Please rate your experience: https://kushiin.com/rate");

        System.out.println("✅ Booking completion notification sent to: " + email);
    }
}
