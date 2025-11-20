package com.kushi.in.app.dao;

import com.kushi.in.app.entity.Login;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {
   boolean existsByEmail(String Email);
   boolean existsByPhoneNumber(String phoneNumber);

   // Login findByEmailAndPassword(String email, String password);


   Optional<Login> findByEmail(String email);
}
 