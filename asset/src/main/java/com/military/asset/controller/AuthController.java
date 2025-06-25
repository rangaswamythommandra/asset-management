package com.military.asset.controller;

import com.military.asset.model.Base;
import com.military.asset.model.User;
import com.military.asset.service.BaseService;
import com.military.asset.service.UserService;
import com.military.asset.security.CustomUserDetailsService;
import com.military.asset.security.JwtUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final BaseService baseService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService, JwtUtil jwtUtil, UserService userService, BaseService baseService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.baseService = baseService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            User user = userService.findByUsername(request.getUsername()).orElseThrow();
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String jwt = jwtUtil.generateTokenWithClaims(userDetails, user.getRole().name(), user.getBase() != null ? user.getBase().getId() : null);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(jwt, refreshToken));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userService.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        Optional<Base> baseOpt = baseService.findById(request.getBaseId());
        if (baseOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Base not found");
        }
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            user.setBase(baseOpt.get());
            userService.save(user);
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String jwt = jwtUtil.generateTokenWithClaims(userDetails, user.getRole().name(), user.getBase() != null ? user.getBase().getId() : null);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(jwt, refreshToken));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            String username = jwtUtil.extractUsernameFromRefreshToken(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtUtil.validateRefreshToken(refreshToken, userDetails)) {
                return ResponseEntity.status(401).body("Invalid or expired refresh token");
            }
            User user = userService.findByUsername(username).orElseThrow();
            String jwt = jwtUtil.generateTokenWithClaims(userDetails, user.getRole().name(), user.getBase() != null ? user.getBase().getId() : null);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(jwt, newRefreshToken));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid or expired refresh token");
        }
    }

    @PostMapping("/test-password")
    public ResponseEntity<?> testPassword(@RequestBody AuthRequest request) {
        try {
            User user = userService.findByUsername(request.getUsername()).orElseThrow();
            boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
            return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "storedPassword", user.getPassword(),
                "inputPassword", request.getPassword(),
                "matches", matches
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/update-admin-password")
    public ResponseEntity<?> updateAdminPassword(@RequestBody AuthRequest request) {
        try {
            User admin = userService.findByUsername("admin").orElseThrow();
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
            userService.save(admin);
            return ResponseEntity.ok("Admin password updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/test/update-admin-password")
    public ResponseEntity<String> updateAdminPassword() {
        try {
            User admin = userService.findByUsername("admin").orElseThrow();
            admin.setPassword(passwordEncoder.encode("admin123"));
            userService.save(admin);
            return ResponseEntity.ok("Admin password updated to 'admin123'");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update password: " + e.getMessage());
        }
    }

    @PostMapping("/test/update-all-passwords")
    public ResponseEntity<String> updateAllPasswords() {
        try {
            List<User> users = userService.findAll();
            for (User user : users) {
                user.setPassword(passwordEncoder.encode("admin123"));
                userService.save(user);
            }
            return ResponseEntity.ok("All user passwords updated to 'admin123'");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update passwords: " + e.getMessage());
        }
    }

    @PostMapping("/test/set-specific-passwords")
    public ResponseEntity<String> setSpecificPasswords() {
        try {
            // Update admin password
            User admin = userService.findByUsername("admin").orElseThrow();
            admin.setPassword(passwordEncoder.encode("admin123"));
            userService.save(admin);
            
            // Update commander1 password
            User commander = userService.findByUsername("commander1").orElseThrow();
            commander.setPassword(passwordEncoder.encode("commander123"));
            userService.save(commander);
            
            // Update logistics1 password
            User logistics = userService.findByUsername("logistics1").orElseThrow();
            logistics.setPassword(passwordEncoder.encode("logistics123"));
            userService.save(logistics);
            
            return ResponseEntity.ok("Passwords updated: admin=admin123, commander1=commander123, logistics1=logistics123");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update passwords: " + e.getMessage());
        }
    }

    @PostMapping("/fix-passwords")
    public ResponseEntity<String> fixPasswords() {
        try {
            // Update admin password to: admin123
            User admin = userService.findByUsername("admin").orElseThrow();
            admin.setPassword(passwordEncoder.encode("admin123"));
            userService.save(admin);
            
            // Update commander1 password to: commander123
            User commander = userService.findByUsername("commander1").orElseThrow();
            commander.setPassword(passwordEncoder.encode("commander123"));
            userService.save(commander);
            
            // Update logistics1 password to: logistics123
            User logistics = userService.findByUsername("logistics1").orElseThrow();
            logistics.setPassword(passwordEncoder.encode("logistics123"));
            userService.save(logistics);
            
            return ResponseEntity.ok("Passwords fixed: admin=admin123, commander1=commander123, logistics1=logistics123");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fix passwords: " + e.getMessage());
        }
    }

    @GetMapping("/check-passwords")
    public ResponseEntity<?> checkPasswords() {
        try {
            User admin = userService.findByUsername("admin").orElseThrow();
            User commander = userService.findByUsername("commander1").orElseThrow();
            User logistics = userService.findByUsername("logistics1").orElseThrow();
            
            return ResponseEntity.ok(Map.of(
                "admin", Map.of(
                    "username", admin.getUsername(),
                    "passwordHash", admin.getPassword(),
                    "admin123_matches", passwordEncoder.matches("admin123", admin.getPassword())
                ),
                "commander1", Map.of(
                    "username", commander.getUsername(),
                    "passwordHash", commander.getPassword(),
                    "commander123_matches", passwordEncoder.matches("commander123", commander.getPassword())
                ),
                "logistics1", Map.of(
                    "username", logistics.getUsername(),
                    "passwordHash", logistics.getPassword(),
                    "logistics123_matches", passwordEncoder.matches("logistics123", logistics.getPassword())
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error checking passwords: " + e.getMessage());
        }
    }

    public static class AuthRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RegisterRequest {
        private String username;
        private String password;
        private User.Role role;
        private Long baseId;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public User.Role getRole() {
            return role;
        }

        public void setRole(User.Role role) {
            this.role = role;
        }

        public Long getBaseId() {
            return baseId;
        }

        public void setBaseId(Long baseId) {
            this.baseId = baseId;
        }
    }

    public static class AuthResponse {
        private final String token;
        private final String refreshToken;

        public AuthResponse(String token, String refreshToken) {
            this.token = token;
            this.refreshToken = refreshToken;
        }

        public String getToken() {
            return token;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }

    public static class RefreshRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
} 