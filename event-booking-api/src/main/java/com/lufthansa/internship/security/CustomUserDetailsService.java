package com.lufthansa.internship.security;

import com.lufthansa.internship.entity.User;
import com.lufthansa.internship.repository.UserRepository;
import com.lufthansa.internship.security.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new AppUserPrincipal(user);
    }

    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()) != null) {
            throw new IllegalArgumentException("This email already exists");
        }

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(AppRole.ADMIN);

        return userRepository.save(guest);
    }
}
