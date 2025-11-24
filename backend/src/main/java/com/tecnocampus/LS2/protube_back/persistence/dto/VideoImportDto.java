package com.tecnocampus.LS2.protube_back.persistence.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * DTO for importing videos from JSON files.
 * Maps to the structure of JSON files like 0.json with nested meta information.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VideoImportDto(
    Long id,
    Integer width,
    Integer height,
    Double duration,
    String title,
    String user,  // Username string from JSON
    MetaDto meta
) {
    /**
     * Nested metadata containing description, categories, tags, and comments
     */
    public record MetaDto(
        String description,
        List<String> categories,
        List<String> tags,
        List<CommentImportDto> comments
    ) {}

    /**
     * Comment data from JSON
     */
    public record CommentImportDto(
        String text,
        String author
    ) {}
}

