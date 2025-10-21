package com.tecnocampus.LS2.protube_back.domain;

import com.tecnocampus.LS2.protube_back.persistence.dto.UserDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "users")
@Getter
@Setter
public class User {
    @Id
    private String username;
    private Long id;
    private String authId;
    private String password;
    @OneToMany(mappedBy = "user")
    private List<Video> videos;
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    public User() {}

    public User(String username, String authId) {
        this.username = username;
        this.authId = authId;
    }

    public User(UserDTO userDTO) {
        this.username = userDTO.username();
    }

    public void updateUser(String username) {
        this.username = username;
    }
}
