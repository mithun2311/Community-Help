package com.community.backend.config;
import java.util.Optional;
import java.util.Collections;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.community.backend.service.JwtService;
import com.community.backend.repository.UserRepository;
import com.community.backend.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    private JwtService jwtService;
    private UserRepository userRepository;
    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService=jwtService;
        this.userRepository=userRepository;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader=request.getHeader("Authorization");
        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String email=jwtService.extractEmail(token);
            Optional<User> user=userRepository.findByEmail(email);
            if(user.isEmpty()) {
                return;
            }
            Authentication authentication=new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
        System.out.println("LOGIN CONTROLLER REACHED");
    }
}
