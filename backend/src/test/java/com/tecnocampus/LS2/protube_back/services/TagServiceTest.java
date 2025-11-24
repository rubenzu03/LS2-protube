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
    @Test
    void deleteTag_existing_deletesAndReturnsDTO() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Tag t = new Tag();
        t.setId(50L);
        t.setName("toDel");
        Mockito.when(tagRepository.findById(50L)).thenReturn(Optional.of(t));

        TagService service = new TagService(tagRepository);

        TagDto out = service.deleteTag(50L);
        assertNotNull(out);
        assertEquals(50L, out.id());
        Mockito.verify(tagRepository, Mockito.times(1)).deleteById(50L);
    }

    @Test
    void deleteTag_notFound_returnsNull() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.findById(60L)).thenReturn(Optional.empty());

        TagService service = new TagService(tagRepository);

        TagDto out = service.deleteTag(60L);
        assertNull(out);
        Mockito.verify(tagRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }

    @Test
    void updateTag_existing_updatesNameAndReturnsDTO() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Tag existing = new Tag();
        existing.setId(70L);
        existing.setName("oldName");

        Mockito.when(tagRepository.findById(70L)).thenReturn(Optional.of(existing));
        Mockito.when(tagRepository.save(ArgumentMatchers.any(Tag.class))).thenAnswer(i -> i.getArgument(0));

        TagDto dto = new TagDto(null, "newName");

        TagService service = new TagService(tagRepository);
        TagDto out = service.updateTag(70L, dto);

        assertNotNull(out);
        assertEquals(70L, out.id());
        assertEquals("newName", out.name());
    }

    @Test
    void updateTag_notFound_returnsNull() {
        TagRepository tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.findById(80L)).thenReturn(Optional.empty());

        TagService service = new TagService(tagRepository);
        TagDto dto = new TagDto(null, "x");

        assertNull(service.updateTag(80L, dto));
    }
}

