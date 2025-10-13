package com.tecnocampus.LS2.protube_back.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "tags")
@Getter
@Setter
public class Tag {
    @Id
    private Long id;
    private String name;
}
