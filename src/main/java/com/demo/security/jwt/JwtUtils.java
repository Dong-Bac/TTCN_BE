package com.demo.security.jwt;

import com.demo.security.user.HotelUserDetails;
import com.fasterxml.jackson.core.JsonFactory;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${auth.token.jwtSecret}")
    private  String jwtSecret;
    @Value("${auth.token.expirationMils}")
    public int jwtExpirationMs;

    public String generateJwtTokenForUser(Authentication authentication){
        HotelUserDetails userPrincipal=(HotelUserDetails) authentication.getPrincipal();
        List<String> roles=userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime()+jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512,jwtSecret).compact();
    }

    public String getUserNameFromToken(String token){
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        }catch(MalformedJwtException e){
            System.out.println("Invalid jwt token : {} "+ e.getMessage());
        }catch (ExpiredJwtException e){
            System.out.println("Expired token : {} "+ e.getMessage());
        }catch (UnsupportedJwtException e){
            System.out.println("This token is not supported : {} "+ e.getMessage());
        }catch (IllegalArgumentException e){
            System.out.println("No  claims found : {} "+ e.getMessage());
        }
        return false;
    }
}
