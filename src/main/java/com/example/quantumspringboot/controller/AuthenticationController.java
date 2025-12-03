package com.example.quantumspringboot.controller;

import com.example.quantumspringboot.dto.AuthenticationRequest;
import com.example.quantumspringboot.dto.AuthenticationResponse;
import com.example.quantumspringboot.dto.UserRequestDTO;
import com.example.quantumspringboot.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/v1/auth/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid
                                                           @RequestBody UserRequestDTO request
    ){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/v1/auth/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate( @Valid
                                                                @RequestBody AuthenticationRequest request
    ){return ResponseEntity.ok(service.authenticate(request));}


}
