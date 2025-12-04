package com.tecnocampus.LS2.protube_back.persistence.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class VideoDTODeserializer extends JsonDeserializer<VideoDTO> {

    @Override
    public VideoDTO deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        Long id = node.has("id") && !node.get("id").isNull()
                ? node.get("id").asLong() : null;

        String title = node.has("title") && !node.get("title").isNull()
                ? node.get("title").asText() : null;

        float width = node.has("width") && !node.get("width").isNull()
                ? (float) node.get("width").asDouble() : 0f;

        float height = node.has("height") && !node.get("height").isNull()
                ? (float) node.get("height").asDouble() : 0f;

        float duration = node.has("duration") && !node.get("duration").isNull()
                ? (float) node.get("duration").asDouble() : 0f;

        String description = node.has("description") && !node.get("description").isNull()
                ? node.get("description").asText() : null;

        String filename = node.has("filename") && !node.get("filename").isNull()
                ? node.get("filename").asText() : null;

        Long userId = node.has("userId") && !node.get("userId").isNull()
                ? node.get("userId").asLong() : null;

        Long categoryId = node.has("categoryId") && !node.get("categoryId").isNull()
                ? node.get("categoryId").asLong() : null;

        Long tagId = node.has("tagId") && !node.get("tagId").isNull()
                ? node.get("tagId").asLong() : null;

        Long commentId = node.has("commentId") && !node.get("commentId").isNull()
                ? node.get("commentId").asLong() : null;

        return new VideoDTO(
                id,
                title,
                width,
                height,
                duration,
                description,
                filename,
                userId,
                categoryId,
                tagId,
                commentId
        );
    }
}
