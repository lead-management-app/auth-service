package com.blitzdev.auth_service.services;

import com.blitzdev.auth_service.domain.User;
import com.blitzdev.auth_service.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    public List<UUID> getAllDemoUsersIds() {
        return userRepo.findAllDemoUsersId();
    }
}