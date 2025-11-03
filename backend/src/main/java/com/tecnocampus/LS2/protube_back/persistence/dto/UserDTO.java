package com.tecnocampus.LS2.protube_back.persistence.dto;

public record UserDTO(
        String username,
        Long id,
        String password,
        Long videoId,
        Long commentId
) {
}
