package com.blitzdev.auth_service.controllers;

import com.blitzdev.auth_service.dtos.RegisterUserDto;
import com.blitzdev.auth_service.dtos.UserDto;
import com.blitzdev.auth_service.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("/lms/api/v1/auth")
@Tag(name = "Authentication") // for api documentation
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Operation(description = "Post endpoint for user creation", responses =
            {@ApiResponse(description = "OK", responseCode = "200")}
    )
    @PostMapping("/signUp")
    public ResponseEntity<?> createNewUser(@RequestBody RegisterUserDto dto, HttpServletRequest request) {

        try {
            Optional<UserDto> optUser = authService.signUp(dto, getBaseUrl(request));
            return ResponseEntity.ok(optUser.get());
        } catch (Exception e) {
            log.error("Error occurred while creating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_EXPECTATION_FAILED).body(e.getMessage());
        }
    }


    @Operation(description = "Endpoint for user verification", responses =
            {@ApiResponse(description = "OK", responseCode = "200")}
    )
    @PostMapping("/verify/{token}")
    public ResponseEntity<?> verifyUser(@PathVariable("token") String token) {

        try {
            authService.verifyUser(token);
            return ResponseEntity.ok("Confirmation successful.");
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_EXPECTATION_FAILED).body(e.getMessage());
        }
    }

    private String getBaseUrl(HttpServletRequest request) {
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(request.getRequestURI())
                .replaceQuery(null)
                .build()
                .toString();
    }
}
