package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.persistence.CommentRepository;
import com.tecnocampus.LS2.protube_back.persistence.UserRepository;
import com.tecnocampus.LS2.protube_back.persistence.VideoRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void getUsers_emptyList_returnsEmpty() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

        Mockito.when(userRepository.findAll()).thenReturn(List.of());

        UserService service = new UserService(userRepository, videoRepository, commentRepository);

        assertTrue(service.getUsers().isEmpty());
    }

    @Test
    void getUserById_notFound_returnsNull() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserService service = new UserService(userRepository, videoRepository, commentRepository);

        assertNull(service.getUserById(1L));
    }

    @Test
    void createUser_savesAndReturnsDTO() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

        // UserDTO(username, id, password, videoId, commentId)
        UserDTO dto = new UserDTO("u", null, null, null, null);
        User saved = new User(dto);
        saved.setId(7L);

        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(saved);

        UserService service = new UserService(userRepository, videoRepository, commentRepository);

        UserDTO result = service.createUser(dto);
        assertNotNull(result);
        assertEquals(7L, result.id());
        assertEquals("u", result.username());
    }
    @Test
    void deleteUser_existing_returnsDTOAndDeletes() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

        User u = new User();
        u.setId(11L);
        u.setUsername("delUser");

        Mockito.when(userRepository.findById(11L)).thenReturn(Optional.of(u));

        UserService service = new UserService(userRepository, videoRepository, commentRepository);

        var dto = service.deleteUser(11L);
        assertNotNull(dto);
        assertEquals(11L, dto.id());
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(11L);
    }
    @Test
    void deleteUser_notFound_returnsNull() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

        Mockito.when(userRepository.findById(20L)).thenReturn(Optional.empty());

        UserService service = new UserService(userRepository, videoRepository, commentRepository);

        var dto = service.deleteUser(20L);
        assertNull(dto);
        Mockito.verify(userRepository, Mockito.never()).deleteById(ArgumentMatchers.anyLong());
    }
    @Test
    void getUserByUsername_found_returnsDTO() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VideoRepository videoRepository = Mockito.mock(VideoRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

        User u = new User();
        u.setId(99L);
        u.setUsername("searchme");

        Mockito.when(userRepository.findByUsername("searchme")).thenReturn(u);

        UserService service = new UserService(userRepository, videoRepository, commentRepository);

        var dto = service.getUserByUsername("searchme");
        assertNotNull(dto);
        assertEquals(99L, dto.id());
        assertEquals("searchme", dto.username());
    }
}
