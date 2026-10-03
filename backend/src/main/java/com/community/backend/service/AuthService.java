package com.community.backend.service;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.community.backend.dto.LoginRequest;
import com.community.backend.dto.RegisterRequest;
import com.community.backend.entity.User;
import com.community.backend.repository.UserRepository;
@Service
public class AuthService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtService=jwtService;
    }
    public String register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            return "User is already registered";
        }
        User user=new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return "User Registered Successfully";
    }
    public String login(LoginRequest request) {
        Optional<User> user=userRepository.findByEmail(request.getEmail());
        if(user.isEmpty()) return "No User Found";
        User existingUser=user.get();
        if(!passwordEncoder.matches(request.getPassword(), existingUser.getPassword())) return "Entered Pssword is incorrect";
        return jwtService.generateToken(request.getEmail());
        
    }
}
