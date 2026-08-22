package com.lufthansa.internship.security;

import com.lufthansa.internship.dto.request.LoginRequestDTO;
import com.lufthansa.internship.dto.request.UserRegisterRequestDTO;
import com.lufthansa.internship.dto.response.TokenResponse;
import com.lufthansa.internship.dto.response.UserResponseDTO;
import com.lufthansa.internship.entity.User;
import com.lufthansa.internship.entity.UserRole;
import com.lufthansa.internship.entity.UserStatus;
import com.lufthansa.internship.exception.AccountDeactivatedException;
import com.lufthansa.internship.exception.DuplicateEmailException;
import com.lufthansa.internship.exception.UserNotFoundException;
import com.lufthansa.internship.mapper.UserMapper;
import com.lufthansa.internship.repository.UserRepository;
import com.lufthansa.internship.security.jwt.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService implements UserDetailsService{

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthService(@Lazy AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new AppUserPrincipal(user);
    }

    public UserResponseDTO register(UserRegisterRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) throw new DuplicateEmailException("This email already exists");
        if (userRepository.findByUsername(request.getUsername()).isPresent()) throw new DuplicateEmailException("This username already exists");

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.ATTENDEE);
        user.setStatus(UserStatus.ACTIVATED);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public TokenResponse login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getStatus() == UserStatus.DEACTIVATED)
            throw new AccountDeactivatedException("Your account has been deactivated");

        UserDetails userDetails = loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);

        user.setLastLogIn(LocalDateTime.now());
        userRepository.save(user);

        return new TokenResponse(token, "Bearer");
    }

}
