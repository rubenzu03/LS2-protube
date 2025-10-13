package com.tecnocampus.LS2.protube_back.persistence.dto;

import com.tecnocampus.LS2.protube_back.domain.Comment;

import java.util.List;

public record VideoDTO(Long id, String title, float width, float height, float duration, String description, Long userId,
                       Long categoryId, List<String> tags, Long commentId) {
}
