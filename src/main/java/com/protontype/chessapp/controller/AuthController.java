package com.protontype.chessapp.controller;

import com.protontype.chessapp.dto.ApiResponse;
import com.protontype.chessapp.model.dto.request.LoginRequest;
import com.protontype.chessapp.model.dto.request.SignupRequest;
import com.protontype.chessapp.model.dto.response.LoginResponse;
import com.protontype.chessapp.model.dto.response.SignupResponse;
import com.protontype.chessapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@RequestBody @Valid SignupRequest request) {
        return ResponseEntity.ok(userService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        ApiResponse<LoginResponse> response = userService.login(request);
        if(response.isSuccess()) {
            String token = userService.generateToken(request.getUsername());
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(response);
        }
        return ResponseEntity.ok(response);
    }

}
