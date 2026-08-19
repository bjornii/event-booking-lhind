package com.lufthansa.internship.security;

import com.lufthansa.internship.dto.request.UserRegisterRequestDTO;
import com.lufthansa.internship.dto.response.UserResponseDTO;
import com.lufthansa.internship.entity.User;
import com.lufthansa.internship.entity.UserRole;
import com.lufthansa.internship.exception.DuplicateEmailException;
import com.lufthansa.internship.mapper.UserMapper;
import com.lufthansa.internship.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new AppUserPrincipal(user);
    }

    public UserResponseDTO register(UserRegisterRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new DuplicateEmailException("This email already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.ATTENDEE);

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }
}
