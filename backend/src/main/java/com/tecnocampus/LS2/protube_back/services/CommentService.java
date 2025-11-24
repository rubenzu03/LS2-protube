package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Comment;
import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.persistence.CommentRepository;
import com.tecnocampus.LS2.protube_back.persistence.UserRepository;
import com.tecnocampus.LS2.protube_back.persistence.VideoRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    public final CommentRepository commentRepository;
    public final UserRepository userRepository;
    public final VideoRepository videoRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository,
            VideoRepository videoRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
    }

    public List<CommentDTO> getComments() {
        return commentRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public CommentDTO getCommentById(Long id) {
        return commentRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public List<CommentDTO> getCommentsByVideoId(Long videoId) {
        Video video = videoRepository.findById(videoId).orElse(null);
        if (video == null) {
            return List.of();
        }
        return commentRepository.findByVideo(video).stream()
                .map(this::toDTO)
                .toList();
    }

    public CommentDTO createComment(CommentDTO dto) {
        // Always take the user from the authenticated principal, never from the client
        // payload
        Comment comment = new Comment();
        comment.setContent(dto.content());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() != null
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            if (user != null) {
                comment.setUser(user);
            }
        }

        if (dto.videoId() != null) {
            comment.setVideo(videoRepository.getReferenceById(dto.videoId()));
        }

        Comment saved = commentRepository.save(comment);
        return toDTO(saved);
    }

    public CommentDTO deleteComment(Long id) {
        CommentDTO commentDTO = getCommentById(id);
        if (commentDTO != null) {
            commentRepository.deleteById(id);
        }
        return commentDTO;
    }

    public CommentDTO updateComment(Long id, CommentDTO dto) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment != null) {
            comment.setContent(dto.content());
            User userComment = userRepository.findById(dto.userId()).orElse(null);
            Video videoComment = videoRepository.findById(dto.videoId()).orElse(null);
            if (userComment != null && videoComment != null) {
                comment.setUser(userComment);
                comment.setVideo(videoComment);
            }
            commentRepository.save(comment);
            return getCommentDTO(dto, comment);
        }
        return null;
    }

    private CommentDTO getCommentDTO(CommentDTO dto, Comment comment) {
        if (dto.userId() != null) {
            comment.setUser(userRepository.getReferenceById(dto.userId()));
        }
        if (dto.videoId() != null) {
            comment.setVideo(videoRepository.getReferenceById(dto.videoId()));
        }
        Comment updated = commentRepository.save(comment);
        return toDTO(updated);
    }

    private CommentDTO toDTO(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getContent(),
                comment.getUser() != null ? comment.getUser().getId() : null,
                comment.getVideo() != null ? comment.getVideo().getId() : null,
                comment.getUser() != null ? comment.getUser().getUsername() : null);
    }

    private Comment toDomain(CommentDTO commentDTO) {
        return new Comment(commentDTO);
    }
}
