package com.kushi.in.app.service;

import com.kushi.in.app.model.AuthResponse;
import com.kushi.in.app.model.SigninRequest;
import com.kushi.in.app.model.SignupRequest;
import com.kushi.in.app.model.ForgotPasswordRequest;

public interface UserService {
    AuthResponse signup(SignupRequest request);
    AuthResponse signin(SigninRequest request);
    String forgotPassword(ForgotPasswordRequest request); // <-- NEW
}
