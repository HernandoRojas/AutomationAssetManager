package com.assetmanager.exception;

public class UserNotFoundException extends UserManagerException {
    public UserNotFoundException(int userId) {
        super("User with ID " + userId + " was not found.");
    }
}