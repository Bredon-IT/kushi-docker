package com.kushi.in.app.dao;

import com.kushi.in.app.entity.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Customer, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Customer c SET c.bookingStatus = :status WHERE c.booking_id = :id")
    void updateBookingStatus(@Param("id") Long bookingId, @Param("status") String status);

}
