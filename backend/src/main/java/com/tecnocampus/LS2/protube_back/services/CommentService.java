package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Comment;
import com.tecnocampus.LS2.protube_back.persistence.CommentRepository;
import com.tecnocampus.LS2.protube_back.persistence.UserRepository;
import com.tecnocampus.LS2.protube_back.persistence.VideoRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class CommentService {

    public final CommentRepository commentRepository;
    public final UserRepository userRepository;
    public final VideoRepository videoRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository, VideoRepository videoRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
    }

    public List<CommentDTO> getComments() {
        return commentRepository.findAll().stream()
                .map(comment -> new CommentDTO(
                        comment.getId(),
                        comment.getContent(),
                        comment.getUser() != null ? comment.getUser().getId() : null,
                        comment.getVideo() != null ? comment.getVideo().getId() : null
                ))
                .toList();
    }

    public CommentDTO getCommentById(Long id) {
        return commentRepository.findById(id)
                .map(comment -> new CommentDTO(
                        comment.getId(),
                        comment.getContent(),
                        comment.getUser() != null ? comment.getUser().getId() : null,
                        comment.getVideo() != null ? comment.getVideo().getId() : null
                ))
                .orElse(null);
    }

    public CommentDTO createComment(CommentDTO dto) {
        Comment comment = new Comment(dto);
        if (dto.userId() != null) {
            comment.setUser(userRepository.getReferenceById(dto.userId()));
        }
        if (dto.videoId() != null) {
            comment.setVideo(videoRepository.getReferenceById(dto.videoId()));
        }
        Comment saved = commentRepository.save(comment);
        return new CommentDTO(
                saved.getId(),
                saved.getContent(),
                saved.getUser() != null ? saved.getUser().getId() : null,
                saved.getVideo() != null ? saved.getVideo().getId() : null
        );
    }

    public CommentDTO deleteComment(Long id) {
        CommentDTO commentDTO = getCommentById(id);
        commentRepository.deleteById(id);
        return commentDTO;
    }

    public CommentDTO updateComment(Long id, CommentDTO dto) {
        Comment comment = new Comment(dto);
        comment.setId(id);
        if (dto.userId() != null) {
            comment.setUser(userRepository.getReferenceById(dto.userId()));
        }
        if (dto.videoId() != null) {
            comment.setVideo(videoRepository.getReferenceById(dto.videoId()));
        }
        Comment saved = commentRepository.save(comment);
        return new CommentDTO(
                saved.getId(),
                saved.getContent(),
                saved.getUser() != null ? saved.getUser().getId() : null,
                saved.getVideo() != null ? saved.getVideo().getId() : null
        );
    }
}
