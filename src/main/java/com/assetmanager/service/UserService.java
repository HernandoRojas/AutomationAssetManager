package com.assetmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.assetmanager.exception.UserNotFoundException;
import com.assetmanager.model.User;
import com.assetmanager.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    private Optional<User> findUserById(int userId) {
        return repository.findById(userId);
    }

    public void registerNewUser(User user) {
        // Business Rule: IDs must be unique (simplified check)
        if (repository.existsById(user.getUserId())) {
            throw new IllegalArgumentException("User ID already exists: " + user.getUserId());
        }
        repository.save(user);
    }

    public User getCreatedUser(int userId) {
        return findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public List<User> findByUsernameAndEmployeeId(String username, String employeeId) {
        if ((username != null && !username.isBlank()) && (employeeId != null && !employeeId.isBlank())) {
            return repository.findByUsernameIgnoreCaseAndEmployeeIdIgnoreCase(username, employeeId);
        } else if (username != null && !username.isBlank()) {
            return repository.findByUsernameIgnoreCase(username);
        } else if (employeeId != null && !employeeId.isBlank()) {
            return repository.findByEmployeeIdIgnoreCase(employeeId);
        }
        return repository.findAll();
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }
}
