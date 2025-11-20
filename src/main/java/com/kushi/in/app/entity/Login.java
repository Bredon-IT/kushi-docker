package com.kushi.in.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name="tbl_admin")
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "adminname", nullable = false, unique = true, length = 50)
    private String adminname;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phoneNumber", nullable = true, unique = true, length = 15)
    private String phoneNumber;

    @Column(name = "password", nullable = false, length = 255)
    private String password;


    private String role;

    public Login() {}

    public Login(Long adminId, String adminname, String email, String phoneNumber, String password, String role) {
        this.adminId = adminId;
        this.adminname = adminname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
    }



    // Getters & Setters
    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getAdminname() {
        return adminname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAdminname(String username) {
        this.adminname = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}