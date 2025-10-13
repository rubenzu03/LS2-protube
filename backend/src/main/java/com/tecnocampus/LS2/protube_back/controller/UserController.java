package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.persistence.dto.UserDTO;
import com.tecnocampus.LS2.protube_back.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("")
    public ResponseEntity<List<User>> getCategories() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.created(userService.createUser(userDTO)).body("User created successfully");
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().body("User deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        userService.updateUser(id, userDTO);
        return ResponseEntity.ok().body("User updated successfully");
    }
}
