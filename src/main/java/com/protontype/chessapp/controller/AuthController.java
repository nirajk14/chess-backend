package com.protontype.chessapp.controller;

import com.protontype.chessapp.dto.ApiResponse;
import com.protontype.chessapp.model.dto.request.SignupRequest;
import com.protontype.chessapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponse<?> signup(@RequestBody  @Valid SignupRequest request) {
        return userService.signup(request);
    }
}
