package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.persistence.dto.CommentDTO;
import com.tecnocampus.LS2.protube_back.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    CommentService commentService;

    @GetMapping()
    public ResponseEntity<List<CommentDTO>> getComments() {
        return ResponseEntity.ok().body(commentService.getComments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getComment(@PathVariable Long id) {
        return ResponseEntity.ok().body(commentService.getCommentById(id));
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        return ResponseEntity.created(commentService.createComment(commentDTO)).body(commentDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommentDTO> deleteComment(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.deleteComment(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long id, @RequestBody CommentDTO commentDTO) {
        return ResponseEntity.ok(commentService.updateComment(id, commentDTO));
    }
}
