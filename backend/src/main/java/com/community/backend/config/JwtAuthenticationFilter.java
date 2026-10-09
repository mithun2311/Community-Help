package com.community.backend.config;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.community.backend.entity.User;
import com.community.backend.repository.UserRepository;
import com.community.backend.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    public JwtAuthenticationFilter(JwtService jwtService,UserRepository userRepository) {
        this.jwtService=jwtService;
        this.userRepository=userRepository;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain) throws ServletException,IOException {
        String authorizationHeader=request.getHeader("Authorization");
        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication()==null) {
            String token=authorizationHeader.substring(7).trim();
            if(!token.isEmpty()) {
                try {
                    String email=jwtService.extractEmail(token);
                    Optional<User> user=userRepository.findByEmail(email);
                    if(user.isPresent()) {
                        Authentication authentication=new UsernamePasswordAuthenticationToken(email,null,Collections.emptyList());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch(JwtException | IllegalArgumentException exception) {
                    SecurityContextHolder.clearContext();
                }
            }
        }
        filterChain.doFilter(request,response);
    }
}
