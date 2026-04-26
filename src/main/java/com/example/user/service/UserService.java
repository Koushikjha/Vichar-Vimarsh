// com/gigshield/user/service/UserService.java
package com.example.user.service;

import com.example.auth.dto.RegisterRequest;
import com.example.user.dto.UpdateProfileRequest;
import com.example.user.dto.UserResponse;
import com.example.user.entity.User;
import com.example.user.mapper.UserMapper;
import com.example.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public UserDetails loadUserByUsername(String phone)
            throws UsernameNotFoundException {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + phone));
    }

    /**
     * Called after OTP is verified.
     * If user exists → login flow, return existing user.
     * If user doesn't exist → registration flow, create and return.
     */
    @Transactional
    public User findOrCreate(String phone, RegisterRequest registrationData) {
        return userRepository.findByPhone(phone)
                .orElseGet(() -> {
                    if (registrationData == null) {
                        throw new IllegalArgumentException(
                                "New user must provide registration details " +
                                        "(fullName, username)");
                    }
                    validateRegistrationData(registrationData);
                    User newUser = userMapper.toEntity(registrationData);
                    log.info("New user registered: phone={}", phone);
                    return userRepository.save(newUser);
                });
    }

    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + phone));
    }

    public Optional<User> getByPhone(String phone){
        return userRepository.findByPhone(phone);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + id));
    }

    public UserResponse getProfile(String phone) {
        return userMapper.toResponse(findByPhone(phone));
    }

    @Transactional
    public UserResponse updateProfile(String phone,
                                      UpdateProfileRequest request) {
        User user = findByPhone(phone);
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        return userMapper.toResponse(userRepository.save(user));
    }


    public long countAll() {
        return userRepository.count();
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private void validateRegistrationData(RegisterRequest req) {
        if (req.getFullName() == null || req.getFullName().isBlank()) {
            throw new IllegalArgumentException(
                    "fullName is required for registration");
        }
        if (req.getUsername() == null || req.getUsername().isBlank()) {
            throw new IllegalArgumentException(
                    "username is required for registration");
        }
    }
}