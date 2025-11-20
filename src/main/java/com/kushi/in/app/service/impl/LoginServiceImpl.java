package com.kushi.in.app.service.impl;

import com.kushi.in.app.dao.LoginRepository;
import com.kushi.in.app.entity.Login;
import com.kushi.in.app.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginRepository loginRepository;


    @Override
    public Login loginAdmin(String email, String password) {
        return loginRepository.findByEmail(email)
                .filter(user -> user.getEmail().equals(email))      // ✅ case-sensitive email
                .filter(user -> user.getPassword().equals(password)) // ✅ case-sensitive password
                .orElse(null);
    }



    @Override
    public Login getAdminByEmail(String email) {
        return loginRepository.findByEmail(email).orElse(null);
    }

    @Override
    public Login updateAdmin(Long id, Login updatedAdmin) {
        return loginRepository.findById(id).map(existingAdmin -> {
            existingAdmin.setAdminname(updatedAdmin.getAdminname());
            existingAdmin.setEmail(updatedAdmin.getEmail());
            existingAdmin.setPhoneNumber(updatedAdmin.getPhoneNumber());
            existingAdmin.setPassword(updatedAdmin.getPassword());
            existingAdmin.setRole(updatedAdmin.getRole());
            return loginRepository.save(existingAdmin);
        }).orElse(null);
    }


    @Override
    public Login updatePassword(Long adminId, String oldPassword, String newPassword) {
        Login admin = loginRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!admin.getPassword().equals(oldPassword)) {
            throw new RuntimeException("Old password is incorrect");
        }

        admin.setPassword(newPassword);
        return loginRepository.save(admin);
    }


    @Override
    public Login addUser(Login login) {
        if (loginRepository.existsByEmail(login.getEmail())) {
            throw new RuntimeException("❌ Email already exists!");
        }
        if (login.getPhoneNumber() != null && loginRepository.existsByPhoneNumber(login.getPhoneNumber())) {
            throw new RuntimeException("❌ Phone number already exists!");
        }
        return loginRepository.save(login);
    }

    @Override
    public List<Login> getAllUsers() {
        return loginRepository.findAll();
    }

    @Override
    public void deleteUser(Long adminId) {
        if (!loginRepository.existsById(adminId)) {
            throw new RuntimeException("❌ User not found");
        }
        loginRepository.deleteById(adminId);
    }



    @Override
    public void updatePassword(String email, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("❌ Passwords do not match");
        }

        Login user = loginRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("❌ User not found"));

        user.setPassword(newPassword); // ⚡ here you can also encode password if needed
        loginRepository.save(user);
    }

}

