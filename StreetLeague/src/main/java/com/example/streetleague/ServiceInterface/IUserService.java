package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.ChangePasswordRequest;
import com.example.streetleague.dto.UpdateProfileRequest;
import com.example.streetleague.dto.UserProfileResponse;

public interface IUserService {
    UserProfileResponse getProfile(String email);
    UserProfileResponse updateProfile(String email, UpdateProfileRequest request);
    void changePassword(String email, ChangePasswordRequest request);
    void deleteAccount(String email);
}
