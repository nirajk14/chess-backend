package com.protontype.chessapp;

import com.protontype.chessapp.service.RedisTestService;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.DriverManager;

@Component
@AllArgsConstructor
public class DbTestRunner implements CommandLineRunner {

    private final RedisTestService redisTestService;

    @Override
    public void run(String... args) throws Exception {
        String url = "jdbc:postgresql://localhost:5432/chessdb";
        String user = "postgres";
        String pass = "postgres";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("✅ Connected to PostgreSQL successfully!");
        }
        redisTestService.testConnection();
    }

}

