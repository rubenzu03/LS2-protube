package com.tecnocampus.LS2.protube_back.domain;

import com.tecnocampus.LS2.protube_back.persistence.dto.UserDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "users")
@Getter
@Setter
public class User {
    @Id
    private Long id;
    private String username;
    @OneToMany(mappedBy = "user")
    private List<Video> videos;
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    public User() {}

    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public User(UserDTO userDTO) {
        this.id = userDTO.id();
        this.username = userDTO.username();
    }

    public void updateUser(String username) {
        this.username = username;
    }
}
