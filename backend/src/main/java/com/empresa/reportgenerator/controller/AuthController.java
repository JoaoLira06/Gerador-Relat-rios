package com.empresa.reportgenerator.controller;

import com.empresa.reportgenerator.dto.auth.AuthenticationRequest;
import com.empresa.reportgenerator.dto.auth.AuthenticationResponse;
import com.empresa.reportgenerator.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest request){

        AuthenticationResponse authenticationResponse = authenticationService.login(request);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader) {

        // Remove o prefixo "Bearer " para pegar só o token
        String token = authHeader.substring(7);
        authenticationService.logout(token);
        return ResponseEntity.noContent().build();
    }
}

