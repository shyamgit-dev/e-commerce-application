package com.sam.service.Impl;

import com.sam.dao.UserRepository;
import com.sam.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JWTService {

    //private static final String SECRET = "mysecretkeymysecretkeymysecretkeymysecretkey";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final UserRepository userRepository;

    private SecretKey getSigningKey()
    {
        //byte [] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateRefreshToken(User user)
    {
        return createToken(user.getUsername(),
                           new HashMap<>(),
                refreshTokenExpiration);// Expiry time 7 days
    }

    public String generateAccessToken(User user)
    {
        Map<String,Object> claims = new HashMap<>();
        claims.put("role",user.getRole());
        claims.put("userId",user.getUserId());
        return createToken(user.getUsername(),claims,accessTokenExpiration); // 15 minutes expiry time
    }

/*    public String generateToken(String username)
    {
        User user = userRepository.findByUsername(username)
                        .orElseThrow(()->new UsernameNotFoundException("Username Not Found"));
        Map<String,Object> claims = new HashMap<>();
        claims.put("role",user.getRole());
        claims.put("userId",user.getUserId());
        return createToken(username,claims);
    }*/

    private String createToken(String username,Map<String,Object> claims ,long expiryTime)
    {
         return Jwts.builder()
                 .claims(claims)
                 .subject(username)
                 .issuedAt(new Date())
                 .expiration(new Date(System.currentTimeMillis()+expiryTime))
                 .issuer("e-commerce-app")
                 .signWith(getSigningKey())
                 .compact();
    }

/*    private String createToken(String username, Map<String,Object> claims)
    {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000*60*60))
                .issuer("e-commerce-app")
                .signWith(getSigningKey())
                .compact();
    }*/

    public String extractUsername(String token)
    {
        return extractClaim(token,Claims::getSubject);
    }

    public Date extractExpiration(String token)
    {
        return extractClaim(token,Claims::getExpiration);
    }

    public boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token,UserDetails userDetails)
    {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public <T> T extractClaim(String token,Function<Claims,T> function)
    {
         return function.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

/*    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username)
    {
         return Jwts.builder()
                 .subject(username)
                 .issuedAt(new Date())
                 .expiration(
                         new Date(System.currentTimeMillis() +1000*60*60)
                 )
                 .signWith(getSigningKey())
                 .compact();
    }

    public String extractUsername(String token)
    {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Date extractExpiration(String token)
    {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    // true->Token Expired and false->Token Still Valid
    public boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails)
    {
        String username = extractUsername(token);

         return username.equals(userDetails.getUsername())
                 && !isTokenExpired(token);
    }*/

}
