package com.example.streetleague.ServiceInterface;


import com.example.streetleague.domain.User;
import com.example.streetleague.dto.*;

public interface IAuthService {
    User register(RegisterRequest req);

    AuthResponse login(LoginRequest req);
    AuthResponse completeGoogleRegister(CompleteGoogleRegisterRequest req);

    void forgotPassword(ForgotPasswordRequest req);
    void resetPassword(ResetPasswordRequest req);
}