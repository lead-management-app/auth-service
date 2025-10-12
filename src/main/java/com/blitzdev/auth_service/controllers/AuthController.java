package com.blitzdev.auth_service.controllers;

import com.blitzdev.auth_service.dtos.LoginUserDto;
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

import java.util.Optional;

@RestController
@RequestMapping("/lms/api/v1/auth")
@Tag(name = "Authentication") // for api documentation
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Operation(description = "Creates a new user with a unique email and password.", responses =
            {@ApiResponse(description = "Returns the created UserDto object", responseCode = "200 Ok"),
            @ApiResponse(description = " Returns an error message if user creation fails.", responseCode = "417 Expectation Failed")}
    )
    @PostMapping("/signup")
    public ResponseEntity<?> createNewUser(@RequestBody RegisterUserDto dto, HttpServletRequest request) {

        try {
            Optional<UserDto> optUser = service.signUp(dto);
            return ResponseEntity.ok(optUser.get());
        } catch (Exception e) {
            log.error("Error occurred while creating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_EXPECTATION_FAILED).body(e.getMessage());
        }
    }


    @Operation(description = "Verifies a user's account using a unique token sent to their email.", responses = {
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "Returns an error message if the verification token is invalid or expired.", responseCode = "417 Expectation Failed")}
    )
    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verifyUser(@PathVariable("token") String token) {

        try {
            service.verifyUser(token);
            return ResponseEntity.ok("Email confirmation successful.");
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_EXPECTATION_FAILED).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Authenticates a user trying to login.", responses = {
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "Returns an error message if the login credentials are invalid", responseCode = "417")}
    )
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(
            @RequestBody LoginUserDto request)
    {
        Optional<UserDto> response = (Optional<UserDto>) service.authenticate(request);
        if(response.isEmpty()){
            return ResponseEntity.status(HttpStatus.SC_EXPECTATION_FAILED).body("Invalid Login Credentials");
        }
        return ResponseEntity.ok(response.get());
    }

    @Operation(
            summary = "Begins the process for a user to reset password. ", responses = {
            @ApiResponse(description = "OK", responseCode = "200"),
            @ApiResponse(description = "Returns an error message if the user is not verifiable.", responseCode = "417")})
    @PostMapping("/forgot-password/{email}")
    public ResponseEntity<?>forgotPassword (@PathVariable("email") String email) throws Exception {
        try {
            boolean success = service.forgotPassword(email);

            if(!success){
                return ResponseEntity.status(HttpStatus.SC_EXPECTATION_FAILED).body("Password reset attempt failed.");
            }
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());
        }
        return ResponseEntity.ok("Password reset successful.");
    }

    @Operation(
            summary = "Endpoint for updating a user's password after changing ", responses = {
            @ApiResponse(description = "OK",responseCode = "200"),
            @ApiResponse(description = "Invalid Login Credentials", responseCode = "417")})
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody LoginUserDto request)
    {
        try {
            return ResponseEntity.ok(service.resetPassword(request));
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_EXPECTATION_FAILED).body("Password reset attempt failed.");
        }
    }
}