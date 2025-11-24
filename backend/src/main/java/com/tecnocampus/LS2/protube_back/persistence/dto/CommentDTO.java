package com.tecnocampus.LS2.protube_back.persistence.dto;

public record CommentDTO(
                Long id,
                String content,
                Long userId,
                Long videoId,
                String username) {
}
