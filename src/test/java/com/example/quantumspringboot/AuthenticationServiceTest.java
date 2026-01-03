package com.example.quantumspringboot;

import com.example.quantumspringboot.dto.*;
import com.example.quantumspringboot.entity.User;
import com.example.quantumspringboot.exceptions.EntityAlreadyExistsException;
import com.example.quantumspringboot.exceptions.EntityDoesNotExistException;
import com.example.quantumspringboot.repository.UserRepository;
import com.example.quantumspringboot.service.AuthenticationService;
import com.example.quantumspringboot.service.JwtService;
import com.example.quantumspringboot.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------- REGISTER ----------------

    @Test
    void register_success() {
        UserRequestDTO request = new UserRequestDTO();
        request.setEmail("test@example.com");
        request.setPassword("password");

        User userEntity = new User();
        userEntity.setEmail("test@example.com");

        when(userRepository.existsUserByEmail(request.getEmail()))
                .thenReturn(false);

        when(objectMapper.convertValue(request, User.class))
                .thenReturn(userEntity);

        when(passwordEncoder.encode("password"))
                .thenReturn("encoded-password");

        when(jwtService.generateToken(userEntity))
                .thenReturn("jwt-token");

        AuthenticationResponse response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());

        verify(userRepository).save(userEntity);
        assertEquals("encoded-password", userEntity.getPassword());
    }

    @Test
    void register_userAlreadyExists() {
        UserRequestDTO request = new UserRequestDTO();
        request.setEmail("test@example.com");

        when(userRepository.existsUserByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> authenticationService.register(request));

        verify(userRepository, never()).save(any());
    }

    // ---------------- AUTHENTICATE ----------------

    @Test
    void authenticate_success() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setEmail("test@example.com");

        User userEntity = new User();
        userEntity.setEmail("test@example.com");

        when(userService.findUserByEmail(request.getEmail()))
                .thenReturn(userResponse);

        when(objectMapper.convertValue(userResponse, User.class))
                .thenReturn(userEntity);

        when(jwtService.generateToken(userEntity))
                .thenReturn("jwt-token");

        // authenticationManager.authenticate(...) returns void → just verify it’s called
        doNothing().when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_userDoesNotExist() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("missing@example.com");
        request.setPassword("password");

        doNothing().when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        when(userService.findUserByEmail(request.getEmail()))
                .thenThrow(new EntityDoesNotExistException("User not found"));

        assertThrows(EntityDoesNotExistException.class,
                () -> authenticationService.authenticate(request));
    }
}

