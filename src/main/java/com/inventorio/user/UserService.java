package com.inventorio.user;

import com.inventorio.security.jwt.JwtUtil;
import com.inventorio.controllers.request.AuthRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(AuthRequest authRequest) {
        validateAuthRequest(authRequest);
        String username = authRequest.getUsername();
        String password = authRequest.getPassword();
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> jwtUtil.generateToken(username))
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    }

    public void register(AuthRequest authRequest) {
        validateAuthRequest(authRequest);
        String username = authRequest.getUsername();
        String password = authRequest.getPassword();
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }

    private void validateAuthRequest(AuthRequest authRequest) {
        if (authRequest.getUsername() == null || authRequest.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (authRequest.getPassword() == null || authRequest.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        // More checks can be added, but let's keep it simple for now
    }
}
