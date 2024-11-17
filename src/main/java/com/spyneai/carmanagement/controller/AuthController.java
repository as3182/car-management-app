package com.spyneai.carmanagement.controller;

import com.spyneai.carmanagement.dto.JwtResponseDTO;
import com.spyneai.carmanagement.dto.UserLoginDTO;
import com.spyneai.carmanagement.dto.UserRegisterDTO;
import com.spyneai.carmanagement.exceptions.InvalidCredentialsException;
import com.spyneai.carmanagement.exceptions.UserAlreadyExistsException;
import com.spyneai.carmanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        try {
            authService.registerUser(userRegisterDTO);
            return ResponseEntity.ok("User registered successfully!");
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        try {
            JwtResponseDTO token = authService.authenticateUser(userLoginDTO);
            return ResponseEntity.ok(token);
        } catch (InvalidCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An unexpected error occurred: " + ex.getMessage()));
        }
    }



}
