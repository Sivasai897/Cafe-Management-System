package com.in.cafe.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Slf4j
@Service
public class JwtUtil {

    private String secret="sivasai@123";

    //For Generating The JWT below code is used

    public String generateToken(String userName,String role){
        log.info("Step7:\ninto the generate token method"+"\nUserName: "+userName+" \tRole: "+role);
        Map<String,Object> claims=new HashMap<>();
        claims.put("role",role);
        return createToken(claims,userName);
    }

    private String createToken(Map<String,Object> claims, String subject){
        log.info("Step8:\ninto the create token method {}"+claims+" Subject "+subject);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000 * 60 *60))
                .signWith(SignatureAlgorithm.HS256,secret).compact();
    }

    //The below functions are used to extract claims like username and other details
    //and to validate the token

    public Claims extractAllClaims(String token){
        log.info("Part 4c:\nInto the Extrac All claims method");
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    public <T> T extractClaims(String token, Function<Claims,T> claimsResolver){
        log.info("Part 4b:\nInto the Extract Claims Method");
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token){
        log.info("Part 4a:\nInto the extract Uesr Name Method");
        return extractClaims(token,Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Boolean isTokenValid(String token, UserDetails userDetails){
        final String userName=extractUsername(token);
        return (userName.equals(userDetails.getUsername())&&!isTokenExpired(token));
    }

}
