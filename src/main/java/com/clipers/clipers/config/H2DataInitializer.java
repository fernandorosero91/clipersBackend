package com.clipers.clipers.config;

import com.clipers.clipers.entity.User;
import com.clipers.clipers.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("h2")
public class H2DataInitializer {

    @Bean
    CommandLineRunner seedDefaultUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String email = "test@clipers.local";
            if (userRepository.findByEmail(email).isEmpty()) {
                User user = User.createCandidate(email, "password123", "Test", "User", passwordEncoder);
                userRepository.save(user);
            }
        };
    }
}