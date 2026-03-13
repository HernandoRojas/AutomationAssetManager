package com.assetmanager.repository;

import com.assetmanager.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Finds by username (ignoring case)
    List<User> findByUsernameIgnoreCase(String username);

    // Finds by employee ID (ignoring case)
    List<User> findByEmployeeIdIgnoreCase(String employeeId);

    // Finds by username and employee ID (both ignoring case)
    List<User> findByUsernameIgnoreCaseAndEmployeeIdIgnoreCase(String username, String employeeId);

    // Finds by user Id    
    List<User> findByUserId(int userId);
}