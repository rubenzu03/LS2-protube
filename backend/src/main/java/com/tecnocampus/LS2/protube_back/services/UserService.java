package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.persistence.UserRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    public final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return null;
        return toDTO(user);
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = toDomain(userDTO);
        User saved = userRepository.save(user);
        return toDTO(saved);
    }

    public UserDTO deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            userRepository.deleteById(id);
            return toDTO(user);
        }
        return null;
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.updateUser(userDTO.username());
            User updated = userRepository.save(user);
            return toDTO(updated);
        }
        return null;
    }

    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), null, null);
    }

    private User toDomain(UserDTO userDTO) {
        return new User(userDTO.id(), userDTO.username());
    }
}
