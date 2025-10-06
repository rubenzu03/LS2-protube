package com.tecnocampus.LS2.protube_back.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    public List<String> getComments() {
        return List.of("comment1", "comment2");
    }

    public String getCommentById(String commentId) {
        // Logic to get a comment by its ID
        return "commentDetails";
    }

    public void createComment(String content) {
        // Logic to create a comment
    }

    public void deleteComment(String commentId) {
        // Logic to delete a comment
    }

    public void updateComment(String commentId, String newContent) {
        // Logic to update a comment
    }
}
