package com.blitzdev.auth_service.services;

import com.blitzdev.auth_service.domain.User;
import com.blitzdev.auth_service.domain.UserRole;
import com.blitzdev.auth_service.dtos.LoginUserDto;
import com.blitzdev.auth_service.dtos.RegisterUserDto;
import com.blitzdev.auth_service.dtos.UserDto;
import com.blitzdev.auth_service.mapper.UserMapper;
import com.blitzdev.auth_service.repo.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    @Value("${confirmation.base-url}")
    private String baseUrl;

    public Optional<UserDto> signUp(RegisterUserDto dto, String path) throws Exception {

        // does user with this email exist?
        Optional<User> existingUser = userRepo.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            throw new Exception("User with this email already exists");
        }

        // proceed to saving
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .confirmationCode(generateConfirmationCode())
                .confirmationCodeExpiration(LocalDateTime.now().plusMinutes(15))
                .enabledInd(0)
                .userRole(UserRole.USER)
                .build();
        user.setSignUpDate(LocalDateTime.now());
        user.setLastModDate(LocalDateTime.now());

        var savedUser = userRepo.save(user);
        if (savedUser != null) {
            sendConfirmationMail(savedUser, path);
        } else {
            throw new Exception("User could not be created.");
        };
        return Optional.of(userMapper.userToUserDto(savedUser));
    }

    public Optional<?> authenticate(LoginUserDto dto) {
        Optional<User> user = Optional.ofNullable(userRepo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found")));

        if (!user.get().isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account");
        }

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    dto.getEmail(),
                    dto.getPassword()
                )
        );

        return user;
    }

    public void verifyUser(String confirmationCode) {
        Optional<User> optionalUser = userRepo.findByConfirmationCode(confirmationCode);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getConfirmationCodeExpiration().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired.");
            }

            if (user.getConfirmationCode().equals(confirmationCode)) {
                user.setEnabledInd(1);
                user.setConfirmationCode(null);
                user.setConfirmationCodeExpiration(null);
                user.setLastModDate(LocalDateTime.now());

                userRepo.save(user);
            } else {
                throw new RuntimeException("Invalid verification code.");
            }
        } else {
            throw new RuntimeException("User not found.");
        }
    }

    public void resendConfirmationCode(String email, String baseUrl) {
        Optional<User> optUser = userRepo.findByEmail(email);

        if (optUser.isPresent()) {
            User user = optUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified.");
            }
            user.setConfirmationCode(generateConfirmationCode());
            user.setConfirmationCodeExpiration(LocalDateTime.now().plusMinutes(15));
            sendConfirmationMail(user, baseUrl);
            userRepo.save(user);
        } else {
            throw new RuntimeException("User not found.");
        }
    }

    public void sendConfirmationMail(User user, String baseUrl) {
        String subject = "Account Confirmation";
        try {
            emailService.sendConfirmationMail(user.getEmail(), subject, getMessage(getConfirmationLink()));
        } catch (MessagingException e) {
            log.error("Confirmation email could not be sent: {}", e.getMessage());
        }
    }

    private String generateConfirmationCode() {
        Random random = new Random();
        return String.valueOf(random.nextInt(900000) + 100000); // 6 digits code
    }

    private String getConfirmationLink() {
        String code = generateConfirmationCode();
         return this.baseUrl + "/verify/" + code;
    }

    private String getMessage(String confirmationLink) {
        return "<html>"
                + "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f5f5f5;\">"
                + "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">"

                // Header
                + "<div style=\"background-color: #007bff; padding: 30px; text-align: center; border-radius: 10px 10px 0 0;\">"
                + "<h1 style=\"color: #ffffff; margin: 0; font-size: 28px;\">Welcome! 🎉</h1>"
                + "</div>"

                // Main Content
                + "<div style=\"background-color: #ffffff; padding: 40px 30px; border-radius: 0 0 10px 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);\">"
                + "<h2 style=\"color: #333; margin-top: 0;\">Confirm Your Email Address</h2>"
                + "<p style=\"font-size: 16px; color: #666; line-height: 1.6;\">"
                + "Thank you for signing up! To get started, please verify your email address by clicking the button below:"
                + "</p>"

                // CTA Button
                + "<div style=\"text-align: center; margin: 30px 0;\">"
                + "<a href=\"" + confirmationLink + "\" "
                + "style=\"display: inline-block; padding: 14px 32px; background-color: #007bff; color: #ffffff; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;\">"
                + "Confirm Email Address"
                + "</a>"
                + "</div>"

                // Alternative Link
                + "<div style=\"margin-top: 30px; padding: 20px; background-color: #f8f9fa; border-left: 4px solid #007bff; border-radius: 4px;\">"
                + "<p style=\"margin: 0 0 10px 0; font-size: 14px; color: #666;\">"
                + "<strong>Button not working?</strong> Copy and paste this link into your browser:"
                + "</p>"
                + "<p style=\"margin: 0; font-size: 13px; color: #007bff; word-break: break-all;\">"
                + confirmationLink
                + "</p>"
                + "</div>"

                // Expiration Warning
                + "<div style=\"margin-top: 25px; padding: 15px; background-color: #fff3cd; border-radius: 5px; border: 1px solid #ffc107;\">"
                + "<p style=\"margin: 0; font-size: 14px; color: #856404; text-align: center;\">"
                + "⏰ <strong>This link will expire in 15 minutes</strong>"
                + "</p>"
                + "</div>"

                // Footer Note
                + "<p style=\"font-size: 13px; color: #999; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; text-align: center;\">"
                + "If you didn't create an account, you can safely ignore this email."
                + "</p>"

                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }
}
