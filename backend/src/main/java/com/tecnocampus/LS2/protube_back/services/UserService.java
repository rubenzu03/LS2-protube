package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.persistence.UserRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.UserDTO;
import com.tecnocampus.LS2.protube_back.persistence.dto.VideoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    public UserRepository userRepository;

    public UserService() {
    }

    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public URI createUser(UserDTO userDTO) {
        User user = toEntity(userDTO);
        userRepository.save(user);
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();
    }

    public UserDTO deleteUser(Long id) {
        UserDTO userDTO = getUserById(id);
        userRepository.deleteById(id);
        return userDTO;
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = toEntity(userDTO);
        user.setId(id);
        userRepository.save(user);
        return toDTO(user);
    }

    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername());
    }

    private User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.id());
        user.setUsername(userDTO.username());
        return user;
    }
}
