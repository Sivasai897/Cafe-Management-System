package com.in.cafe.JWT;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    CustomerUserDetailService service;

    Claims claims = null;
    private String userName = null;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Step F:\nInto the doFilterInternal Method");
        if (request.getServletPath().matches("/user/login|/user/forgotPassword")) {
            log.info("Step G:\nPassing request and response to doFilter  Method");
            filterChain.doFilter(request, response);
        } else {
            log.info("Part 1:\ninto Else of doFilter Method");
            String authorizationHeader = request.getHeader("Authorization");
            log.info("Part 2:\nRetrieved the authorization Header"+authorizationHeader);
            String token = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                log.info("Part 3:\nchecked authrizationheader is null or start with Bearer");
                token = authorizationHeader.substring(7);
                log.info("Part 4:\nRetrieved the token from Authorization header"+"\nToken:"+token);
                userName = jwtUtil.extractUsername(token);
                claims = jwtUtil.extractAllClaims(token);
                log.info("Part 5:\nRetrieved the user Name and Claims"+"\nUserName:"+userName+"\nClaims{}:"+claims);
            }

            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("Part 6:\nInto security context Holder condition"+
                          SecurityContextHolder.getContext().getAuthentication());
                UserDetails userDetails = service.loadUserByUsername(userName);
                if (jwtUtil.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            log.info("Part 7:\nSuccesfully Validated the token and passing request and repsonse to filterChain");
            filterChain.doFilter(request, response);
        }

    }

    public Boolean isAdmin() {
        return "admin" .equalsIgnoreCase((String) claims.get("role"));
    }

    public Boolean isUser() {
        return "user" .equalsIgnoreCase((String) claims.get("user"));
    }

    public String getCurrentUser() {
        return userName;
    }
}
