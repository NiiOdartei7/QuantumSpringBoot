package com.example.quantumspringboot.service;

import com.example.quantumspringboot.dto.AuthenticationRequest;
import com.example.quantumspringboot.dto.AuthenticationResponse;
import com.example.quantumspringboot.dto.UserRequestDTO;
import com.example.quantumspringboot.dto.UserResponseDTO;
import com.example.quantumspringboot.entity.User;
import com.example.quantumspringboot.exceptions.EntityAlreadyExistsException;
import com.example.quantumspringboot.exceptions.EntityDoesNotExistException;
import com.example.quantumspringboot.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(UserRequestDTO request) {

        if(userRepository.existsUserByEmail(request.getEmail())){
            throw new EntityAlreadyExistsException("User Already Exists");

        }

//        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User convertedUser = objectMapper.convertValue(request, User.class);
        convertedUser.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(convertedUser);
        var jwtToken = jwtService.generateToken(convertedUser);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        try {
            UserResponseDTO user = userService.findUserByEmail(request.getEmail());
            var jwtToken = jwtService.generateToken(objectMapper.convertValue(user, User.class));
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (EntityDoesNotExistException e) {
            throw new EntityDoesNotExistException("User Does Not Exist");

        }
    }
}