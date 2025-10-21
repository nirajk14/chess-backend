package com.protontype.chessapp.service;

import com.protontype.chessapp.model.User;
import com.protontype.chessapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String signup(String username, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match!";
        }

        if (userRepository.findByUsername(username).isPresent()) {
            return "Username already exists!";
        }

        User user = User.builder()
                .username(username)
                .password(password)
                .build();

        userRepository.save(user);
        return "User registered successfully!";
    }
}