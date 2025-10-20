package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.persistence.UserRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    public final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), null, null))
                .toList();
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return null;
        return new UserDTO(user.getId(), user.getUsername(), null, null);
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = new User(userDTO.id(), userDTO.username());
        User saved = userRepository.save(user);
        return new UserDTO(saved.getId(), saved.getUsername(), null, null);
    }

    public UserDTO deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            userRepository.deleteById(id);
            return new UserDTO(user.getId(), user.getUsername(), null, null);
        }
        return null;
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = new User(id, userDTO.username());
        User updated = userRepository.save(user);
        return new UserDTO(updated.getId(), updated.getUsername(), null, null);
    }
}
