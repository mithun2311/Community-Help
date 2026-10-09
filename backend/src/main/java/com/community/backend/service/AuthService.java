package com.community.backend.service;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.community.backend.dto.LoginRequest;
import com.community.backend.dto.RegisterRequest;
import com.community.backend.entity.User;
import com.community.backend.entity.UserRole;
import com.community.backend.entity.AccountType;
import com.community.backend.repository.UserRepository;
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final java.util.Set<String> adminEmails;
    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtService jwtService,@Value("${app.admin.emails:}") String adminEmails) {
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtService=jwtService;
        this.adminEmails=java.util.Arrays.stream(adminEmails.split(",")).map(String::trim).filter(value->!value.isEmpty()).map(value->value.toLowerCase(Locale.ROOT)).collect(java.util.stream.Collectors.toUnmodifiableSet());
    }
    @Transactional
    public String register(RegisterRequest request) {
        if(request.getName()==null || request.getName().trim().isEmpty()) throw new IllegalArgumentException("Name is required");
        String email=normalizeEmail(request.getEmail());
        if(request.getPassword()==null || request.getPassword().length()<6 || request.getPassword().length()>72) throw new IllegalArgumentException("Password must contain between 6 and 72 characters");
        if(userRepository.existsByEmail(email)) throw new ResponseStatusException(HttpStatus.CONFLICT,"An account with this email already exists");
        User user=new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        AccountType accountType=request.getAccountType()==null?AccountType.INDIVIDUAL:request.getAccountType();
        if(accountType==AccountType.NGO && (request.getOrganizationName()==null || request.getOrganizationName().trim().isEmpty())) throw new IllegalArgumentException("Organization name is required for NGO accounts");
        user.setAccountType(accountType);
        user.setOrganizationName(accountType==AccountType.NGO?request.getOrganizationName().trim():null);
        user.setRole(adminEmails.contains(email)?UserRole.ADMIN:UserRole.USER);
        if(adminEmails.contains(email)) user.setVerificationStatus(com.community.backend.entity.VerificationStatus.VERIFIED);
        userRepository.save(user);
        return "User Registered Successfully";
    }
    public String login(LoginRequest request) {
        String email=normalizeEmail(request.getEmail());
        if(request.getPassword()==null || request.getPassword().isEmpty()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password");
        User user=userRepository.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));
        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password");
        if(adminEmails.contains(email) && (user.getRole()!=UserRole.ADMIN || user.getVerificationStatus()!=com.community.backend.entity.VerificationStatus.VERIFIED)) { user.setRole(UserRole.ADMIN); user.setVerificationStatus(com.community.backend.entity.VerificationStatus.VERIFIED); userRepository.save(user); }
        return jwtService.generateToken(user.getEmail());
    }
    private String normalizeEmail(String email) {
        if(email==null || email.trim().isEmpty() || !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new IllegalArgumentException("A valid email address is required");
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
