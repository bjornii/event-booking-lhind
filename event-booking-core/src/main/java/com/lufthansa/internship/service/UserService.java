package com.lufthansa.internship.service;

import com.lufthansa.internship.dto.request.UserRegisterRequestDTO;
import com.lufthansa.internship.dto.request.UserUpdateDTO;
import com.lufthansa.internship.dto.response.UserResponseDTO;
import com.lufthansa.internship.entity.User;
import com.lufthansa.internship.entity.UserRole;
import com.lufthansa.internship.entity.UserStatus;
import com.lufthansa.internship.exception.DuplicateEmailException;
import com.lufthansa.internship.exception.UserNotFoundException;
import com.lufthansa.internship.mapper.UserMapper;
import com.lufthansa.internship.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO createUser(UserRegisterRequestDTO request, UserRole role){
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("User creation rejected: email '{}' already exists", request.getEmail());
            throw new DuplicateEmailException("Email already exists");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            logger.warn("User creation rejected: username '{}' already exists", request.getUsername());
            throw new DuplicateEmailException("Username already exists");
        }

        User user = userMapper.toEntity(request);
        user.setStatus(UserStatus.ACTIVATED);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        User saved = userRepository.save(user);
        logger.info("Admin created user {} with role {}", saved.getId(), role);
        return userMapper.toDTO(saved);
    }

    @Transactional
    public Page<UserResponseDTO> getAllUsers(){
        Pageable pageable = PageRequest.of(0, 20, Sort.by("firstName"));
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toDTO);
    }

    @Transactional
    public UserResponseDTO updateUser(Integer userId, UserUpdateDTO dto){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User does not exist"));

        if (dto.getEmail() != null && userRepository.existsByEmail(dto.getEmail()))
            throw new DuplicateEmailException("Email already exists");
        if (dto.getUsername() != null && userRepository.existsByUsername(dto.getUsername()))
            throw new DuplicateEmailException("Username already exists");

        if (dto.getUsername() != null) user.setUsername(dto.getUsername().trim());
        if (dto.getPassword() != null) user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getEmail() != null) user.setEmail(dto.getEmail().trim());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber().trim());
        if (dto.getRole() != null) {
            logger.info("Admin changing role of user {} to {}", userId, dto.getRole());
            user.setRole(dto.getRole());
        }
        if (dto.getStatus() != null) {
            logger.info("Admin changing status of user {} to {}", userId, dto.getStatus());
            user.setStatus(dto.getStatus());
        }
        user.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        logger.info("User {} updated", userId);
        return userMapper.toDTO(saved);
    }

    @Transactional
    public void deactivateUser(Integer userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User does not exist"));
        user.setStatus(UserStatus.DEACTIVATED);
        userRepository.save(user);
        logger.info("User {} deactivated", userId);
    }
}