package com.kushi.in.app.dao;

import com.kushi.in.app.entity.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Customer, Long> {

    // ⭐ Revenue By Service
    @Query(value = """
            SELECT 
                c.booking_service_name AS serviceName,
                SUM(c.booking_amount) AS totalRevenue
            FROM tbl_booking_info c
            GROUP BY c.booking_service_name
            """, nativeQuery = true)
    List<Object[]> getRevenueByService();


    // ⭐ Today's Bookings
    @Query(value = """
            SELECT COUNT(*) 
            FROM tbl_booking_info 
            WHERE DATE(booking_date) = CURRENT_DATE
            """, nativeQuery = true)
    long countTodayBookings();


    // ⭐ Pending Approvals
    @Query(value = """
            SELECT COUNT(*) 
            FROM tbl_booking_info 
            WHERE LOWER(booking_status) = 'pending'
            """, nativeQuery = true)
    long countPendingApprovals();


    // ⭐ Top 5 customers with most bookings
    @Query(value = """
            SELECT 
                b.customer_email,
                MAX(b.customer_name) AS customer_name,
                COUNT(b.customer_email) AS booking_count,
                MAX(b.address_line_1) AS address_line_1,
                MAX(b.customer_number) AS customer_number,
                SUM(b.booking_amount) AS totalAmount
            FROM tbl_booking_info b
            GROUP BY b.customer_email
            ORDER BY booking_count DESC
            LIMIT 5
            """, nativeQuery = true)
    List<Object[]> findTopBookedCustomers();


    // ⭐ Top Services (most booked)
    @Query(value = """
            SELECT 
                s.service_name,
                s.service_image_url,
                s.service_description,
                COUNT(c.booking_id) AS totalBookings
            FROM tbl_booking_info c
            JOIN tbl_service_info s ON c.service_id = s.service_id
            GROUP BY s.service_id
            ORDER BY totalBookings DESC
            """, nativeQuery = true)
    List<Object[]> findTopServices(Pageable pageable);


    // ⭐ Top Rated Services (rating is varchar → convert to decimal)
    @Query(value = """
            SELECT 
                s.service_name,
                s.service_type,
                s.service_cost,
                s.service_image_url,
                AVG(CAST(s.rating AS DECIMAL(10,2))) AS avg_rating,
                COUNT(s.rating) AS rating_count
            FROM tbl_service_info s
            GROUP BY s.service_id
            ORDER BY avg_rating DESC
            LIMIT 5
            """, nativeQuery = true)
    List<Object[]> findTopRatedServices();

}

