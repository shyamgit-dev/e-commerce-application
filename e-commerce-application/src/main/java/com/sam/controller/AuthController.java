package com.sam.controller;

import com.sam.dao.RefreshTokenRepository;
import com.sam.dao.UserRepository;
import com.sam.dto.LogOutRequestDTO;
import com.sam.dto.LoginRequestDTO;
import com.sam.dto.LoginResponseDTO;
import com.sam.dto.RefreshTokenRequestDTO;
import com.sam.entity.RefreshToken;
import com.sam.entity.User;
import com.sam.service.Impl.CustomUserDetailsService;
import com.sam.service.Impl.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenRepository tokenRepository;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO requestDTO)
    {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getUsername(),
                        requestDTO.getPassword()));

        User user = userRepository.findByUsername(requestDTO.getUsername())
                .orElseThrow(()-> new UsernameNotFoundException("UserName not found"));
        
        String accessToken = "";
        String refreshToken = "";
        
        if(authentication.isAuthenticated())
        {
            accessToken = jwtService.generateAccessToken(user);
            refreshToken = jwtService.generateRefreshToken(user);

            RefreshToken refToken = new RefreshToken();
            refToken.setUser(user);
            refToken.setToken(refreshToken);
            refToken.setExpiryDate(LocalDateTime.now().plusDays(7));
            tokenRepository.save(refToken); //Saving Refresh Token to DB
        }
        
        return new ResponseEntity<>(new LoginResponseDTO(accessToken,refreshToken),HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO)
    {
        String refreshToken = refreshTokenRequestDTO.getToken();

        RefreshToken refToken = tokenRepository.findByToken(refreshToken)
                .orElseThrow(()-> new RuntimeException("Refresh Token Not Found"));

        if(!refToken.getExpiryDate().isAfter(LocalDateTime.now()))
            throw new RuntimeException("Token Already Expired");

        String username = jwtService.extractUsername(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if(!jwtService.validateToken(refreshToken,userDetails))
        {
            throw new RuntimeException("Tokens Expired");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("Username not found"));

        String accessToken = jwtService.generateAccessToken(user);

        return new ResponseEntity<>(new LoginResponseDTO(accessToken,refreshToken),HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogOutRequestDTO requestDTO)
    {
          tokenRepository.findByToken(requestDTO.getRefreshToken())
                  .ifPresent(tokenRepository::delete);
          return new ResponseEntity<>("Logged Out",HttpStatus.CREATED);
    }

/*    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO requestDTO)
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                requestDTO.getUsername(),
                requestDTO.getPassword()
        ));

        String token = jwtService.generateToken(requestDTO.getUsername());

        return new ResponseEntity<>(new LoginResponseDTO(token), HttpStatus.CREATED);
    }*/

/*    @GetMapping("/test")
    public String extractUsername(@RequestHeader("Authorization") String header)
    {
        String token = header.substring(7);
        return jwtService.extractUsername(token);
    }*/
}
