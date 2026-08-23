package com.lufthansa.internship.config;

import com.lufthansa.internship.entity.UserRole;
import com.lufthansa.internship.entity.UserStatus;
import com.lufthansa.internship.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lufthansa.internship.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "testing"})
@AllArgsConstructor
public class RoleDataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RoleDataSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUser("admin@test.com", "Admin123$", UserRole.ADMIN);
        seedUser("organizer@test.com", "Organizer123$", UserRole.ORGANIZER);
        seedUser("attendee@test.com", "Attendee123$", UserRole.ATTENDEE);
    }

    private void seedUser(String email, String rawPassword, UserRole role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .status(UserStatus.ACTIVATED)
                .build();
        userRepository.save(user);
        logger.info("Seeded {} user: {}", role, email);
    }

}
