package com.blitzdev.auth_service.config;

import com.blitzdev.auth_service.domain.User;
import com.blitzdev.auth_service.domain.UserRole;
import com.blitzdev.auth_service.repo.UserRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Locale;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
public class UserDataSeeder {

    private final BCryptPasswordEncoder passwordEncoder;
    Faker faker = new Faker(new Locale("en"));
    private final UserRepository userRepository;

    @Bean
    public CommandLineRunner loadDummyUsers() {
        return args -> {

            if (demoEntitiesExist()) {


                String password = passwordEncoder.encode("password");

                // Check if Varata already exists
                if (userRepository.findByEmail("varata@demo.com").isEmpty()) {
                    User varata = User.builder()
                            .name("Varata Inc.")
                            .email("varata@demo.com")
                            .password(password)
                            .userRole(UserRole.SUPER_ADMIN)
                            .lockedInd(0)
                            .enabledInd(1)
                            .deltaPasswordInd(0)
                            .build();
                    userRepository.save(varata);
                }

                // Add dummy users if not already present.
                // 1000 random dummy users.
                IntStream.rangeClosed(1, 1000).forEach(i -> {
                    String fullName = faker.name().fullName();
                    String email = faker.internet().emailAddress(fullName.replaceAll("\\s+", ".").toLowerCase());
                    String encodedPassword = passwordEncoder.encode("password" + i);

                    if (userRepository.findByEmail(email).isEmpty()) {
                        userRepository.save(User.builder()
                                .name(fullName)
                                .email(email)
                                .password(encodedPassword)
                                .userRole(UserRole.USER)
                                .lockedInd(0)
                                .enabledInd(1)
                                .deltaPasswordInd(0)
                                .demoInd(1)
                                .build());
                    }
                });
                System.out.println("✅ Dummy users loaded successfully (Varata + 20 users).");
            }
        };
    }


    private boolean demoEntitiesExist() {
        return userRepository.isDemoUserExists() == 1;
    }

}
