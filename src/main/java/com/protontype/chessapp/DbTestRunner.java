package com.protontype.chessapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.DriverManager;

@Component
public class DbTestRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        String url = "jdbc:postgresql://localhost:5432/chessdb";
        String user = "postgres";
        String pass = "postgres";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("✅ Connected to PostgreSQL successfully!");
        }
    }

}

