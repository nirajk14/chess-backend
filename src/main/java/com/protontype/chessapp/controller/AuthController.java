package com.protontype.chessapp.controller;

import com.protontype.chessapp.service.UserService;
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
    public String signup(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String confirmPassword = body.get("confirmPassword");

        return userService.signup(username, password, confirmPassword);
    }
}
