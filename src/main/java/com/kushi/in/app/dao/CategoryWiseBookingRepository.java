package com.kushi.in.app.dao;

import com.kushi.in.app.model.CategoryWiseBookingDTO;
import com.kushi.in.app.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryWiseBookingRepository extends JpaRepository<Customer, Long> {

    // ✅ MAIN QUERY (returns category-wise completed & cancelled counts)
    @Query("""
        SELECT new com.kushi.in.app.model.CategoryWiseBookingDTO(
            s.service_category,
            SUM(CASE WHEN c.bookingStatus = 'completed' THEN 1 ELSE 0 END),
            SUM(CASE WHEN c.bookingStatus = 'cancelled' THEN 1 ELSE 0 END)
        )
        FROM Customer c
        JOIN Services s ON c.booking_service_name = s.service_type
        GROUP BY s.service_category
        """)
    List<CategoryWiseBookingDTO> getCategoryWiseBookings();



    @Query("""
    SELECT new com.kushi.in.app.model.CategoryWiseBookingDTO(
        s.service_category,
        SUM(CASE WHEN c.bookingStatus = 'completed' THEN 1 ELSE 0 END),
        SUM(CASE WHEN c.bookingStatus = 'cancelled' THEN 1 ELSE 0 END)
    )
    FROM Customer c
    JOIN Services s ON c.booking_service_name = s.service_type
    WHERE MONTH(c.bookingDate) = :month AND YEAR(c.bookingDate) = :year
    GROUP BY s.service_category
    """)
    List<CategoryWiseBookingDTO> getCategoryWiseBookingsByMonthYear(int month, int year);
}
