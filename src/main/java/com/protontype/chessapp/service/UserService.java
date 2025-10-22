package com.protontype.chessapp.service;

import com.protontype.chessapp.dto.ApiResponse;
import com.protontype.chessapp.mapper.SignupMapper;
import com.protontype.chessapp.model.dto.request.SignupRequest;
import com.protontype.chessapp.model.dto.response.SignupResponse;
import com.protontype.chessapp.model.entity.User;
import com.protontype.chessapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SignupMapper signupMapper;

    public ApiResponse<?> signup(SignupRequest signupRequest) {
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            return ApiResponse.failure("Passwords do not match!");
        }

        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            return ApiResponse.failure("Username already exists!");
        }
        String hashedPassword = passwordEncoder.encode(signupRequest.getPassword());

        User user = User.builder()
                .username(signupRequest.getUsername())
                .password(hashedPassword)
                .build();

        userRepository.save(user);
        return ApiResponse.success("User registered successfully!", signupMapper.toResponse(user));
    }
}