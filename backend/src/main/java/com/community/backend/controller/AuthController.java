package com.community.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.backend.dto.LoginRequest;
import com.community.backend.dto.RegisterRequest;
import com.community.backend.service.AuthService;
import com.community.backend.service.JwtService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;
    private JwtService jwtService;
    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService=authService;
        this.jwtService=jwtService;
    }
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        System.out.println("LOGIN CONTROLLER REACHED");
        return authService.login(request);
    }
}
