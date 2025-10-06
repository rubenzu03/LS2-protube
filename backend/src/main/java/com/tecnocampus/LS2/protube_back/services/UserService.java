package com.tecnocampus.LS2.protube_back.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    public List<String> getUsers() {
        return List.of("user1", "user2");
    }

    public String getUserById(String userId) {
        // Logic to get a user by its ID
        return "userDetails";
    }

    public void createUser(String username) {
        // Logic to create a user
    }

    public void deleteUser(String userId) {
        // Logic to delete a user
    }

    public void updateUser(String userId, String newUsername) {
        // Logic to update a user
    }
}
