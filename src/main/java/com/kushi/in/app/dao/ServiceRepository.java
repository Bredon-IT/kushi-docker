package com.kushi.in.app.dao;

import com.kushi.in.app.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Services, Long> {


    // Use "Y" for active/enabled services
    List<Services> findByActive(String active);
}
