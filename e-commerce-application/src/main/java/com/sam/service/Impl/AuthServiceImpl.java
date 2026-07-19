package com.sam.service.Impl;

import com.sam.dao.RefreshTokenRepository;
import com.sam.dao.UserRepository;
import com.sam.dto.LogOutRequestDTO;
import com.sam.dto.LoginRequestDTO;
import com.sam.dto.LoginResponseDTO;
import com.sam.dto.RefreshTokenRequestDTO;
import com.sam.entity.RefreshToken;
import com.sam.entity.User;
import com.sam.exception.InvalidActionException;
import com.sam.exception.InvalidTokenException;
import com.sam.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("authService")
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final CustomUserDetailsService userDetailsService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO requestDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getUsername(),requestDTO.getPassword()));

        User user = userRepository.findByUsername(requestDTO.getUsername())
                .orElseThrow(()->new UsernameNotFoundException("Username not found!!"));

        RefreshToken token = refreshTokenRepository.findByUser(user)
                .orElse(null);

        String accessToken = "";
        String refreshToken = "";

        if(authentication.isAuthenticated())
        {
            accessToken = jwtService.generateAccessToken(user);

            //Since we are fetching tokens directly from DB there is no need to validate it
            if(token!=null && !jwtService.isTokenExpired(token.getToken())) {
                refreshToken = token.getToken();
            }
            else if(token==null)
            {
                refreshToken = jwtService.generateRefreshToken(user);

                RefreshToken rfToken = new RefreshToken();
                rfToken.setUser(user);
                rfToken.setToken(refreshToken);
                rfToken.setExpiryDate(LocalDateTime.now().plusDays(7));
                refreshTokenRepository.save(rfToken);
            }
        }
        return new LoginResponseDTO(accessToken,refreshToken);
    }

    @Override
    public LoginResponseDTO generateAccessTokenUsingRefreshToken(RefreshTokenRequestDTO tokenRequestDTO) {

        String refreshToken = tokenRequestDTO.getToken();

        RefreshToken refreshToken1 = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(()->new InvalidActionException("Refresh Token Not Found/ Invalid Tokens"));

        if(refreshToken1.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new InvalidTokenException("Refresh Tokens Expired ,Login to generate new Token");

        String username = jwtService.extractUsername(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if(!jwtService.validateToken(refreshToken,userDetails))
            throw new InvalidTokenException("Refresh Tokens Expired ,Login to generate new Token");

        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("Username Not Found"));

        String accessToken = jwtService.generateAccessToken(user);

        return new LoginResponseDTO(accessToken,refreshToken);
    }

    @Override
    public void logout(LogOutRequestDTO requestDTO) {
         refreshTokenRepository.findByToken(requestDTO.getRefreshToken())
                 .ifPresent(refreshTokenRepository::delete);
    }
}
