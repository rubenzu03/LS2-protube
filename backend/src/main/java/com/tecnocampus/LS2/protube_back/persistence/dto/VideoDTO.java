package com.tecnocampus.LS2.protube_back.persistence.dto;

public record VideoDTO(
        Long id,
        String title,
        float width,
        float height,
        float duration,
        String description,
        String videoUrl,
        Long userId,
        Long categoryId,
        Long tagId,
        Long commentId
) {
}
