package com.kushi.in.app.service;

import com.kushi.in.app.entity.Login;
import java.util.List;

public interface LoginService {


  Login loginAdmin(String email, String password);


  Login getAdminByEmail(String email);

  Login updateAdmin(Long id, Login updatedAdmin);

  // LoginService.java
  Login updatePassword(Long adminId, String oldPassword, String newPassword);

  Login addUser(Login login);

  List<Login> getAllUsers();

  void deleteUser(Long targetAdminId);

  void updatePassword(String email, String newPassword, String confirmPassword);
}
 