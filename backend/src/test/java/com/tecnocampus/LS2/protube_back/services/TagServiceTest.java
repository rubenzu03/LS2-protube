package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Tag;
import com.tecnocampus.LS2.protube_back.persistence.TagRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.TagDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TagServiceTest {

    @Test
    void getTags_emptyList_returnsEmpty() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.findAll()).thenReturn(List.of());

        TagService service = new TagService(tagRepository);

        assertTrue(service.getTags().isEmpty());
    }

    @Test
    void getTagById_notFound_returnsNull() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        TagService service = new TagService(tagRepository);

        assertNull(service.getTagById(1L));
    }

    @Test
    void createTag_savesAndReturnsDTO() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        TagDto dto = new TagDto(null, "name");
        Tag saved = new Tag(dto);
        saved.setId(11L);

        Mockito.when(tagRepository.save(ArgumentMatchers.any(Tag.class))).thenReturn(saved);

        TagService service = new TagService(tagRepository);

        TagDto result = service.createTag(dto);
        assertNotNull(result);
        assertEquals(11L, result.id());
        assertEquals("name", result.name());
    }
}

