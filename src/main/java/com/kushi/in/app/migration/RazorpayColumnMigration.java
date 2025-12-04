package com.kushi.in.app.migration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class RazorpayColumnMigration implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            System.out.println("🔄 Checking if Razorpay columns exist...");
            
            // Check if columns already exist
            String checkSql = "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                            "WHERE TABLE_SCHEMA = 'Kushi_dev' " +
                            "AND TABLE_NAME = 'tbl_booking_info' " +
                            "AND COLUMN_NAME = 'razorpay_order_id'";
            
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            
            if (count == 0) {
                System.out.println("➕ Adding Razorpay columns to tbl_booking_info...");
                
                jdbcTemplate.execute("ALTER TABLE tbl_booking_info ADD COLUMN razorpay_order_id VARCHAR(255) NULL");
                jdbcTemplate.execute("ALTER TABLE tbl_booking_info ADD COLUMN razorpay_payment_id VARCHAR(255) NULL");
                jdbcTemplate.execute("ALTER TABLE tbl_booking_info ADD COLUMN razorpay_signature VARCHAR(512) NULL");
                
                System.out.println("✅ Razorpay columns added successfully!");
            } else {
                System.out.println("✅ Razorpay columns already exist, skipping migration.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error during Razorpay column migration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
