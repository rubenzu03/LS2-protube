package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.domain.Tag;
import com.tecnocampus.LS2.protube_back.persistence.TagRepository;
import com.tecnocampus.LS2.protube_back.persistence.dto.TagDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<TagDto> getTags() {
        return tagRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public TagDto getTagById(Long id) {
        return tagRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public TagDto createTag(TagDto tagDto) {
        Tag tag = toDomain(tagDto);
        Tag saved = tagRepository.save(tag);
        return toDTO(saved);
    }

    public TagDto deleteTag(Long id) {
        TagDto tagDto = getTagById(id);
        if (tagDto != null) {
            tagRepository.deleteById(id);
        }
        return tagDto;
    }

    public TagDto updateTag(Long id, TagDto tagDto) {
        Tag tag = tagRepository.findById(id).orElse(null);
        if (tag != null) {
            tag.setName(tagDto.name());
            Tag updated = tagRepository.save(tag);
            return toDTO(updated);
        }
        return null;
    }

    private TagDto toDTO(Tag tag) {
        return new TagDto(tag.getId(), tag.getName());
    }

    private Tag toDomain(TagDto tagDto) {
        return new Tag(tagDto);
    }
}
