package com.example.streetleague.ServiceInterface;


import com.example.streetleague.domain.User;
import com.example.streetleague.dto.AuthResponse;
import com.example.streetleague.dto.LoginRequest;
import com.example.streetleague.dto.RegisterRequest;

public interface IAuthService {
    User register(RegisterRequest req);

    AuthResponse login(LoginRequest req);

}