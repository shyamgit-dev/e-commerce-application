package com.sam.config;

import com.sam.filter.JWTFilter;
import com.sam.service.Impl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    private final JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception
    {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth-> auth

                                // Public
                                .requestMatchers(HttpMethod.POST,"/api/users/sign-up").permitAll()
                                .requestMatchers(HttpMethod.POST,"/api/users").permitAll()
                                .requestMatchers(HttpMethod.GET,"/api/products/**").permitAll()
                                .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
                                .requestMatchers(HttpMethod.POST,"/api/auth/refresh").permitAll()
                                .requestMatchers(HttpMethod.POST,"/api/auth/logout").permitAll()

                                // USER
                                .requestMatchers(HttpMethod.POST,"/api/users/*/orders").hasRole("USER")
                                .requestMatchers(HttpMethod.GET,"/api/users/*/orders").hasRole("USER")
                                .requestMatchers(HttpMethod.PATCH,"/api/users/*/orders/*/cancel")
                                .hasAnyRole("USER","ADMIN")


                                // ADMIN
                                .requestMatchers(HttpMethod.POST,"/api/products").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT,"/api/products/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE,"/api/products/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET,"/api/orders/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT,"/api/users/**").hasRole("ADMIN")

                                .anyRequest().authenticated()
                        )
                //.httpBasic(Customizer.withDefaults());
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();

    }

    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authenticationProvider=
                new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception
    {
        return config.getAuthenticationManager();
    }


/*    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder)
    {
        UserDetails user = User.withUsername("shyam123")
                .password(encoder.encode("123456"))
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("mira123")
                .password(encoder.encode("123456"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user,admin);
    }*/

    @Bean
    public  PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
