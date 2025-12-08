package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Comment;
import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.persistence.CommentRepository;
import com.tecnocampus.LS2.protube_back.persistence.UserRepository;
import com.tecnocampus.LS2.protube_back.persistence.VideoRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.CommentDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void getComments_nonEmpty_mapsToDTO() {
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);

        User u = new User(10L, "user");
        Video v = new Video();
        v.setId(20L);

        Comment c = new Comment();
        c.setId(1L);
        c.setContent("hello");
        c.setUser(u);
        c.setVideo(v);

        when(commentRepository.findAll()).thenReturn(List.of(c));

        CommentService service = new CommentService(commentRepository, userRepository, videoRepository);

        List<CommentDTO> comments = service.getComments();
        assertEquals(1, comments.size());
        CommentDTO dto = comments.iterator().next();
        assertEquals(1L, dto.id());
        assertEquals("hello", dto.content());
        assertEquals(10L, dto.userId());
        assertEquals(20L, dto.videoId());
    }

    @Test
    void getCommentById_found_returnsDTO() {
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);

        Comment c = new Comment();
        c.setId(2L);
        c.setContent("c2");
        when(commentRepository.findById(2L)).thenReturn(Optional.of(c));

        CommentService service = new CommentService(commentRepository, userRepository, videoRepository);

        CommentDTO dto = service.getCommentById(2L);
        assertNotNull(dto);
        assertEquals(2L, dto.id());
        assertEquals("c2", dto.content());
        assertNull(dto.userId());
        assertNull(dto.videoId());
    }

    @Test
    void createComment_withUserAndVideo_setsReferencesAndReturnsDTO() {
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);

        CommentDTO input = new CommentDTO(null, "new", 5L, 6L,"test");

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("u");
        when(auth.getName()).thenReturn("u");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("u")).thenReturn(new User(5L, "u"));

        Video vid = new Video(); vid.setId(6L);
        when(videoRepository.getReferenceById(6L)).thenReturn(vid);

        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment arg = invocation.getArgument(0);
            if (arg.getId() == null) arg.setId(100L);
            return arg;
        });

        CommentService service = new CommentService(commentRepository, userRepository, videoRepository);

        try {
            CommentDTO out = service.createComment(input);
            assertNotNull(out);
            assertEquals(100L, out.id());
            assertEquals("new", out.content());
            assertEquals(5L, out.userId());
            assertEquals(6L, out.videoId());

            verify(commentRepository, atLeastOnce()).save(any(Comment.class));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void createComment_withoutUserVideo_returnsDTOWithNulls() {
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);

        CommentDTO input = new CommentDTO(null, "noRefs", null, null,"test");

        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment arg = invocation.getArgument(0);
            arg.setId(200L);
            return arg;
        });

        CommentService service = new CommentService(commentRepository, userRepository, videoRepository);
        CommentDTO out = service.createComment(input);

        assertNotNull(out);
        assertEquals(200L, out.id());
        assertEquals("noRefs", out.content());
        assertNull(out.userId());
        assertNull(out.videoId());
    }

    @Test
    void deleteComment_found_deletesAndReturnsDTO() {
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);

        Comment c = new Comment();
        c.setId(3L);
        c.setContent("toDelete");
        when(commentRepository.findById(3L)).thenReturn(Optional.of(c));

        CommentService service = new CommentService(commentRepository, userRepository, videoRepository);

        CommentDTO out = service.deleteComment(3L);
        assertNotNull(out);
        assertEquals(3L, out.id());
        verify(commentRepository, times(1)).deleteById(3L);
    }

    @Test
    void deleteComment_notFound_returnsNullAndDoesNotDelete() {
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);

        when(commentRepository.findById(4L)).thenReturn(Optional.empty());

        CommentService service = new CommentService(commentRepository, userRepository, videoRepository);

        CommentDTO out = service.deleteComment(4L);
        assertNull(out);
        verify(commentRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateComment_notFound_returnsNull() {
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);

        Mockito.when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        CommentService service = new CommentService(commentRepository, userRepository, videoRepository);

        CommentDTO dto = new CommentDTO(null, "x", null, null,"test");
        assertNull(service.updateComment(99L, dto));
    }

    @Test
    void updateComment_success_updatesContentAndAssociations() {
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);

        Comment existing = new Comment();
        existing.setId(10L);
        existing.setContent("old");

        when(commentRepository.findById(10L)).thenReturn(Optional.of(existing));

        when(userRepository.findById(7L)).thenReturn(Optional.of(new User(7L, "u7")));
        Video v7 = new Video(); v7.setId(8L);
        when(videoRepository.findById(8L)).thenReturn(Optional.of(v7));

        when(userRepository.getReferenceById(7L)).thenReturn(new User(7L, "u7"));
        when(videoRepository.getReferenceById(8L)).thenReturn(v7);

        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommentService service = new CommentService(commentRepository, userRepository, videoRepository);

        CommentDTO dto = new CommentDTO(null, "updated", 7L, 8L,"test");
        CommentDTO out = service.updateComment(10L, dto);

        assertNotNull(out);
        assertEquals(10L, out.id());
        assertEquals("updated", out.content());
        assertEquals(7L, out.userId());
        assertEquals(8L, out.videoId());

        verify(commentRepository, atLeast(1)).save(any(Comment.class));
    }

    @Test
    void updateComment_withNullIds_updatesOnlyContentAndKeepsNullRefs() {
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);

        Comment existing = new Comment();
        existing.setId(11L);
        existing.setContent("old2");

        when(commentRepository.findById(11L)).thenReturn(Optional.of(existing));

        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommentService service = new CommentService(commentRepository, userRepository, videoRepository);

        CommentDTO dto = new CommentDTO(null, "onlyContent", null, null,"test");
        CommentDTO out = service.updateComment(11L, dto);

        assertNotNull(out);
        assertEquals(11L, out.id());
        assertEquals("onlyContent", out.content());
        assertNull(out.userId());
        assertNull(out.videoId());
    }

    @Test
    void comment_domain_constructor_and_updates_work() {
        // exercise Comment domain methods from inside service tests to increase coverage
        CommentDTO dto = new CommentDTO(null, "fromDto", null, null, null);
        Comment c = new Comment(dto);
        assertEquals("fromDto", c.getContent());

        User u = new User(1L, "user1");
        c.updateUser(u);
        assertSame(u, c.getUser());

        CommentDTO dto2 = new CommentDTO(null, "changed", null, null, null);
        c.updateComment(dto2);
        assertEquals("changed", c.getContent());

        c.setId(999L);
        assertEquals(999L, c.getId());
    }

}
