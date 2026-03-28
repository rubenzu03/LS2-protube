package com.tecnocampus.LS2.protube_back.domain;

import com.tecnocampus.LS2.protube_back.persistence.dto.UserDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;
    @OneToMany(mappedBy = "user")
    private List<Video> videos;
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    public User() {}

    // Constructor used by tests
    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }



    public User(UserDTO userDTO) {
        this.username = userDTO.username();
        this.password = userDTO.password();
    }

    public void updateUser(String username) {
        this.username = username;
    }
}
