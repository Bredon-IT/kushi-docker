package com.kushi.in.app.dao;

import com.kushi.in.app.entity.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    // JpaRepository already provides save(), findAll(), findById(), deleteById(), etc.
}
