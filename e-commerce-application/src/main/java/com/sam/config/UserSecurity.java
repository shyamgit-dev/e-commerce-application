package com.sam.config;

import com.sam.dao.UserRepository;
import com.sam.entity.User;
import com.sam.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    public boolean isOwner(Long userId)
    {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new UserNotFoundException("User Not Found"));

        return user.getUserId().equals(userId);
    }
}
