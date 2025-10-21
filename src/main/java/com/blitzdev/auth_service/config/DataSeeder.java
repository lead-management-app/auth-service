package com.blitzdev.auth_service.config;

import com.blitzdev.auth_service.domain.User;
import com.blitzdev.auth_service.domain.UserRole;
import com.blitzdev.auth_service.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;
import java.util.stream.IntStream;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner loadDummyUsers(UserRepository userRepository) {
        return args -> {
            String encodedPassword = new BCryptPasswordEncoder().encode("password");

            // Check if Varata already exists
            if (userRepository.findByEmail("varata@demo.com").isEmpty()) {
                User varata = User.builder()
                        .name("Varata Inc.")
                        .email("varata@demo.com")
                        .password(encodedPassword)
                        .userRole(UserRole.SUPER_ADMIN)
                        .lockedInd(0)
                        .enabledInd(1)
                        .deltaPasswordInd(0)
                        .build();
                userRepository.save(varata);
            }

            // Add dummy users if not already present
            IntStream.rangeClosed(1, 20).forEach(i -> {
                String email = "user" + i + "@demo.com";
                if (userRepository.findByEmail(email).isEmpty()) {
                    User dummy = User.builder()
                            .name("User " + i)
                            .email(email)
                            .password(encodedPassword)
                            .userRole(UserRole.USER)
                            .lockedInd(0)
                            .enabledInd(1)
                            .deltaPasswordInd(0)
                            .build();
                    userRepository.save(dummy);
                }
            });

            System.out.println("✅ Dummy users loaded successfully (Varata + 20 users).");
        };
    }

}
