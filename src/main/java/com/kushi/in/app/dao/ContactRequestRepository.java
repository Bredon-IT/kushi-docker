package com.kushi.in.app.dao;

import com.kushi.in.app.entity.ContactRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRequestRepository extends JpaRepository<ContactRequest, Long> {

    @Query("SELECT c FROM ContactRequest c ORDER BY c.submittedAt DESC")
    List<ContactRequest> getAllRequests();

    // Find read/unread ordered newest first
    List<ContactRequest> findByIsReadOrderBySubmittedAtDesc(boolean isRead);

    // Optional convenience:
    @Query("SELECT c FROM ContactRequest c WHERE c.submittedAt >= :since ORDER BY c.submittedAt DESC")
    List<ContactRequest> findSince(java.time.LocalDateTime since);
}
