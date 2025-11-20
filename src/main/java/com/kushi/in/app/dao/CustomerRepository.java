package com.kushi.in.app.dao;

import com.kushi.in.app.entity.Customer;
import com.kushi.in.app.model.CustomerDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // ✅ Sync user_id based on email
    @Modifying
    @Transactional
    @Query(value = "UPDATE tbl_booking_info b " +
            "JOIN tbl_users u ON b.customer_email = u.email " +
            "SET b.user_id = u.id " +
            "WHERE b.customer_email = :email", nativeQuery = true)
    void updateUserIdByEmail(@Param("email") String email);

    // ✅ User-based queries
    List<Customer> findByUser_Id(Long userId);
    List<Customer> findByBookingStatus(String bookingStatus);
    List<Customer> findByUserIsNotNull();
    List<Customer> findByUserIsNull();
    List<Customer> findAllByOrderByBookingDateDesc();

    // Completed bookings
    @Query("SELECT c FROM Customer c WHERE c.bookingStatus = 'Completed'")
    List<Customer> findCompletedBookings();

    // DTO projection
    @Query("SELECT new com.kushi.in.app.model.CustomerDTO(" +
            "c.booking_id, c.user.id, c.customer_name, c.customer_email, " +
            "c.customer_number, c.address_line_1, c.city, c.totalAmount, " +
            "c.bookingDate, c.bookingStatus, c.booking_time" +
            ") FROM Customer c")
    List<CustomerDTO> findAllCustomersDTO();

    // Category + date filter
    @Query("SELECT c FROM Customer c WHERE (:category IS NULL OR c.serviceCategory = :category) " +
            "AND (:startDate IS NULL OR c.bookingDate >= :startDate) " +
            "AND (:endDate IS NULL OR c.bookingDate <= :endDate)")
    List<Customer> findBookingsByCategoryAndDate(@Param("category") String category,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    // Sync all bookings
    @Modifying
    @Transactional
    @Query(value = """
            UPDATE tbl_booking_info b
            JOIN tbl_users u ON b.customer_email = u.email
            SET b.user_id = u.id
            WHERE b.user_id IS NULL
            """, nativeQuery = true)
    void syncUserIdsWithEmails();

    // Fetch bookings by user email
    @Query("SELECT c FROM Customer c JOIN c.user u WHERE u.email = :email ORDER BY c.bookingDate DESC")
    List<Customer> findBookingsByUserEmail(String email);

    // ⭐ Added (resolved merge conflict)
    List<Customer> findAllByRatingIsNotNullAndFeedbackIsNotNull();
}
