package com.tecnocampus.LS2.protube_back.persistence.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = VideoDTODeserializer.class)
public record VideoDTO(
        Long id,
        String title,
        float width,
        float height,
        float duration,
        String description,
        String filename,
        Long userId,
        Long categoryId,
        Long tagId,
        Long commentId
) {
}
