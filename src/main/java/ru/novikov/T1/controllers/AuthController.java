package ru.novikov.T1.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.novikov.T1.dto.JwtResponse;
import ru.novikov.T1.dto.LoginRequest;
import ru.novikov.T1.dto.MessageResponse;
import ru.novikov.T1.dto.SignupRequest;
import ru.novikov.T1.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public JwtResponse authenticateUser(@RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/registration")
    public MessageResponse registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.registerUser(signupRequest);
    }
}
