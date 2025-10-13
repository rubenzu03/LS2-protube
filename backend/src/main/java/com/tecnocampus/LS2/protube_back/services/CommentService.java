package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Comment;
import com.tecnocampus.LS2.protube_back.persistence.CommentRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    public CommentRepository commentRepository;

    public CommentService() {
    }

    public List<CommentDTO> getComments() {
        return commentRepository.findAll().stream().map(this::toDTO).toList();
    }

    public CommentDTO getCommentById(Long id) {
        return commentRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public URI createComment(CommentDTO commentDTO) {
        Comment comment = toEntity(commentDTO);
        commentRepository.save(comment);
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(comment.getId())
                .toUri();
    }

    public CommentDTO deleteComment(Long id) {
        CommentDTO commentDTO = getCommentById(id);
        commentRepository.deleteById(id);
        return commentDTO;
    }

    public CommentDTO updateComment(Long id, CommentDTO commentDTO) {
        Comment comment = toEntity(commentDTO);
        comment.setId(id);
        commentRepository.save(comment);
        return toDTO(comment);
    }

    private CommentDTO toDTO(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getContent(),
                comment.getUser() != null ? comment.getUser().getId() : null,
                comment.getVideo() != null ? comment.getVideo().getId() : null
        );
    }

    private Comment toEntity(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setId(commentDTO.id());
        comment.setContent(commentDTO.content());
        return comment;
    }
}
