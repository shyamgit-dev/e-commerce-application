package com.sam.utility;

import com.sam.dao.UserRepository;
import com.sam.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityIntegration {

    private final UserRepository userRepository;

    public Authentication getAuthentication()
    {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !authentication.isAuthenticated())
            throw new AccessDeniedException("Access Denied , Unauthorized User");
        return authentication;
    }

    public User getAuthenticatedUser()
    {
        String username = getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("Username Not Found"));
    }
}
