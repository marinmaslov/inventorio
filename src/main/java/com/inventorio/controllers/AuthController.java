package com.inventorio.controllers;

import com.inventorio.controllers.request.AuthRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.inventorio.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "/auth",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        try {
            String token = userService.login(authRequest);
            logger.info("Successfully authenticated user: {}", authRequest.getUsername());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            logger.error("Unable to login!", e);
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest authRequest) {
        try {
            userService.register(authRequest);
            logger.info("Successfully created user: {}", authRequest.getUsername());
            return ResponseEntity.ok("User " + authRequest.getUsername() + " registered successfully");
        } catch (Exception e) {
            logger.error("Failed to create user", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
