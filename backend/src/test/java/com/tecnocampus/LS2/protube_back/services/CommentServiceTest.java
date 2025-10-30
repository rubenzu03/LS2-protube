package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.persistence.CommentRepository;
import com.tecnocampus.LS2.protube_back.persistence.UserRepository;
import com.tecnocampus.LS2.protube_back.persistence.VideoRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.CommentDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {

    @Test
    void getComments_emptyList_returnsEmpty() {
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);

        Mockito.when(commentRepository.findAll()).thenReturn(List.of());

        CommentService service = new CommentService(commentRepository, userRepository, videoRepository);

        assertTrue(service.getComments().isEmpty());
    }

    @Test
    void updateComment_notFound_returnsNull() {
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);

        Mockito.when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        CommentService service = new CommentService(commentRepository, userRepository, videoRepository);

        CommentDTO dto = new CommentDTO(null, "x", null, null);
        assertNull(service.updateComment(99L, dto));
    }
}

