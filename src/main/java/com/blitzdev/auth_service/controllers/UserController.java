package com.blitzdev.auth_service.controllers;

import com.blitzdev.auth_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lms/api/v1/auth/user")
@Tag(name = "Users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService service;

    @Operation(
            summary = "get all the demo users id of the Varata for demo purposes.",
            responses = {
                    @ApiResponse(description = "Return a list of user ids of all demo users.", responseCode = "200 Ok"),
                    @ApiResponse(description = "Returns an error message if no id is found", responseCode = "404 Not Found.")
            }
    )
    @GetMapping("/demo/usersIds")
    private ResponseEntity<?> getDemoUsersId() {

        logger.info("Fetching demo users.");
        List<UUID> userIds = service.getAllDemoUsersIds();

        if (userIds.isEmpty()) {
            logger.warn("No demo users available.");
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("No demo users available.");
        }
        logger.info("Fetched demo users successfully.");
        return ResponseEntity.ok(userIds);
    }
}