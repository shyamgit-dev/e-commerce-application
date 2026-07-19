package com.sam.service;

import com.sam.dto.LogOutRequestDTO;
import com.sam.dto.LoginRequestDTO;
import com.sam.dto.LoginResponseDTO;
import com.sam.dto.RefreshTokenRequestDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO requestDTO);
    LoginResponseDTO generateAccessTokenUsingRefreshToken(RefreshTokenRequestDTO tokenRequestDTO);
    void logout(LogOutRequestDTO requestDTO);
}
