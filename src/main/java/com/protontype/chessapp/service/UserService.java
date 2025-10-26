package com.protontype.chessapp.service;

import com.protontype.chessapp.model.dto.response.ApiResponse;
import com.protontype.chessapp.mapper.SignupMapper;
import com.protontype.chessapp.model.dto.request.LoginRequest;
import com.protontype.chessapp.model.dto.request.SignupRequest;
import com.protontype.chessapp.model.dto.response.LoginResponse;
import com.protontype.chessapp.model.dto.response.SignupResponse;
import com.protontype.chessapp.model.entity.User;
import com.protontype.chessapp.repository.UserRepository;
import com.protontype.chessapp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SignupMapper signupMapper;
    private final JwtUtil jwtUtil;

    public ApiResponse<SignupResponse> signup(SignupRequest signupRequest) {
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

    public ApiResponse<LoginResponse> login(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
        if (optionalUser.isEmpty()) {
            return ApiResponse.failure("Invalid username or password");
        }
        User user = optionalUser.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.failure("Invalid username or password");
        }

        LoginResponse response = LoginResponse.builder()
                .username(user.getUsername())
                .build();

        return ApiResponse.success("Login successful", response);
    }

    public String generateToken(String username) {
        return jwtUtil.generateToken(username);
    }

}